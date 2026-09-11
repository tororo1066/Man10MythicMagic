package tororo1066.man10mythicmagic.magic.holdlock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.jetbrains.annotations.NotNull;

/**
 * 期限付きの<b>持ち替え禁止</b>のレジストリ兼リスナー
 * <h2>止めるもの</h2>
 * <b>{@link PlayerItemHeldEvent}</b>
 *
 * <h2>設計ルール</h2>
 * <ol>
 *   <li><b>無期限は選べない。</b>{@link #MAX_DURATION_TICKS}(1分)でクランプする
 *       {@code 0} や負値、無期限は選べない。</li>
 *   <li><b>{@code key} はセット扱い。</b>いずれかのキーが生きていればロック
 *       解除はそのキーだけを外す(2つの武器が同時にロックしても片方の解除で両方外れない)</li>
 *   <li><b>同じキーでの再付与は期限を上書き</b>積み上がらない</li>
 *   <li>アンロックは任意。書き忘れても必ず期限切れする</li>
 *   <li>死亡・退出時は全解除(このクラス自身がイベントを拾う)</li>
 *   <li><b>判定はイベント時にレジストリを読むだけ。</b>周期処理は行わない</li>
 * </ol>
 */
public final class HoldLockManager implements Listener {

    /** {@code duration} 未指定時の有効時間(tick)。 */
    public static final int DEFAULT_DURATION_TICKS = 200;

    /** 有効時間の上限(tick)。これを超える指定はここまで切り詰める。 */
    public static final int MAX_DURATION_TICKS = 1200;

    private static final long MILLIS_PER_TICK = 50L;

    private static final HoldLockManager INSTANCE = new HoldLockManager();

    /** インスタンス。アクションもリスナー登録もここから引く。 */
    @NotNull
    public static HoldLockManager get() {
        return INSTANCE;
    }

    private HoldLockManager() {
    }

    /** 1プレイヤー分のロック 1 件。 */
    public static final class Lock {

        @NotNull
        private final String key;

        /** 失効時刻(エポックミリ秒)。 */
        private final long expiresAt;

        private Lock(@NotNull String key, long expiresAt) {
            this.key = key;
            this.expiresAt = expiresAt;
        }

        @NotNull
        public String getKey() {
            return key;
        }

        public boolean isExpired(long now) {
            return now >= expiresAt;
        }

        /** 残り時間(tick)。表示用。 */
        public long getRemainingTicks() {
            return Math.max(0L, (expiresAt - System.currentTimeMillis()) / MILLIS_PER_TICK);
        }
    }

    /** プレイヤー → (キー → ロック)。キーは小文字化して持つ。 */
    @NotNull
    private final Map<UUID, Map<String, Lock>> locks = new HashMap<>();

    // ------------------------------------------------------------- レジストリ

    /**
     * 有効時間を 1 以上 {@link #MAX_DURATION_TICKS} 以下に収める。
     *
     * <p>{@code 0} 以下は 1 tick 扱い。無期限にはしない。
     */
    public static int clampDuration(int durationTicks) {
        return Math.max(1, Math.min(durationTicks, MAX_DURATION_TICKS));
    }

    /**
     * 持ち替え禁止を掛ける。同じキーが既にあれば<b>期限ごと上書き</b>する。
     *
     * @param durationTicks 有効時間(tick)。{@link #clampDuration(int)} でクランプされる
     */
    public void lock(@NotNull UUID playerId, @NotNull String key, int durationTicks) {
        long expiresAt = System.currentTimeMillis()
                + (long) clampDuration(durationTicks) * MILLIS_PER_TICK;

        String normalizedKey = normalizeKey(key);
        locks.computeIfAbsent(playerId, id -> new LinkedHashMap<>())
                .put(normalizedKey, new Lock(normalizedKey, expiresAt));
    }

    /**
     * キーを 1 つ解除する。
     *
     * @return 実際に生きているロックを外したなら true
     */
    public boolean unlock(@NotNull UUID playerId, @NotNull String key) {
        Map<String, Lock> playerLocks = locks.get(playerId);
        if (playerLocks == null) {
            return false;
        }
        Lock removed = playerLocks.remove(normalizeKey(key));
        if (playerLocks.isEmpty()) {
            locks.remove(playerId);
        }
        return removed != null && !removed.isExpired(System.currentTimeMillis());
    }

    /**
     * そのプレイヤーのロックを全て解除する。
     *
     * @return 外した(生きていた)ロックの数
     */
    public int unlockAll(@NotNull UUID playerId) {
        Map<String, Lock> playerLocks = locks.remove(playerId);
        if (playerLocks == null) {
            return 0;
        }
        long now = System.currentTimeMillis();
        int alive = 0;
        for (Lock lock : playerLocks.values()) {
            if (!lock.isExpired(now)) {
                alive++;
            }
        }
        return alive;
    }

    /**
     * 持ち替えが禁止されているか。<b>イベントごとに呼ばれる想定</b>なので、
     * ついでに失効したロックをここで捨てる。
     *
     * <p>周期タスクを持たないため、<b>失効分の掃除はこのメソッドと
     * {@link #getLocks(UUID)} が担う</b>。どちらも呼ばれないまま残っても、
     * 退出時に {@link #unlockAll(UUID)} で捨てるのでレジストリは膨らまない。
     */
    public boolean isLocked(@NotNull UUID playerId) {
        Map<String, Lock> playerLocks = locks.get(playerId);
        if (playerLocks == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        boolean locked = false;
        // 失効分を掃除したいので、見つかっても最後まで回す(件数は高々数件)。
        for (Iterator<Lock> it = playerLocks.values().iterator(); it.hasNext();) {
            Lock lock = it.next();
            if (lock.isExpired(now)) {
                it.remove();
            } else {
                locked = true;
            }
        }
        if (playerLocks.isEmpty()) {
            locks.remove(playerId);
        }
        return locked;
    }

    /** 生きているロックの一覧。表示用。 */
    @NotNull
    public List<Lock> getLocks(@NotNull UUID playerId) {
        Map<String, Lock> playerLocks = locks.get(playerId);
        if (playerLocks == null) {
            return Collections.emptyList();
        }
        long now = System.currentTimeMillis();
        playerLocks.values().removeIf(lock -> lock.isExpired(now));
        if (playerLocks.isEmpty()) {
            locks.remove(playerId);
            return Collections.emptyList();
        }
        return List.copyOf(playerLocks.values());
    }

    /**
     * 全プレイヤーのロックを捨てる。
     */
    public void clear() {
        locks.clear();
    }

    // --------------------------------------------------------------- リスナー

    /**
     * ロック中なら選択スロットの変更を止める。
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (isLocked(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendActionBar(Component.text("持ち替えロック中です").color(NamedTextColor.RED));
        }
    }

    /** 死亡時は全解除　*/
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        unlockAll(event.getEntity().getUniqueId());
    }

    /**
     * 退出時も捨てる。時間で必ず失効する以上いずれ消えるが、
     * レジストリに死んだ UUID を残さないため。
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        unlockAll(event.getPlayer().getUniqueId());
    }

    @NotNull
    private static String normalizeKey(@NotNull String key) {
        return key.trim().toLowerCase(Locale.ROOT);
    }
}
