package tororo1066.man10mythicmagic.magic.holdlock;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.action.CompoundAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.api.spell.TargetType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 期限付きの<b>持ち替え禁止</b>を掛ける / 外すスペルアクション。
 *
 * <p>スペル yml から {@code class: HoldLock} で呼ぶ。
 *
 * <pre>{@code
 * # 持ち替えを禁止する
 * - class: HoldLock
 *   key: hoge
 *   duration: 60      # tick
 *
 * # 解除する
 * - class: HoldLock
 *   lock: false
 *   key: hoge
 * }</pre>
 *
 * <table>
 *   <caption>パラメータ</caption>
 *   <tr><th>名前</th><th>既定</th><th>内容</th></tr>
 *   <tr><td>{@code lock}</td><td>true</td><td>掛けるか外すか</td></tr>
 *   <tr><td>{@code key}</td><td>必須</td><td>名前空間キー。大文字小文字は区別しない</td></tr>
 *   <tr><td>{@code duration}</td><td>200</td><td>有効時間(tick)。上限 1200(1分)でクランプ</td></tr>
 * </table>
 *
 * <h2>止めるもの</h2>
 *{@code PlayerItemHeldEvent}
 *
 * <h2>対象</h2>
 * ワンドの所持には依存せず、<b>スペルのターゲット</b>に掛かる。自己拘束なら {@code target: self}、
 * デバフとして相手に掛けるなら通常のターゲティングでよい。{@code target: none}(既定)の場合だけ
 * 詠唱者本人にフォールバックする。ターゲットを取り損ねた場合は {@link SpellResult#NO_TARGET} を返す
 * (どちらも {@code stop = false} なのでアクションの連鎖は止まらない)。
 */
public class HoldLock extends CompoundAction {

    private static final String PARAM_LOCK = "lock";
    private static final String PARAM_KEY = "key";
    private static final String PARAM_DURATION = "duration";

    private boolean lock = true;

    private String key;

    private int durationTicks = HoldLockManager.DEFAULT_DURATION_TICKS;

    /**
     * 設定ミスの警告は 1 インスタンスにつき 1 回だけ出す。
     * prepare は詠唱のたびに呼ばれるので、そのままだとログが埋まる。
     */
    private boolean warned;

    @Override
    public void prepare(CastContext context, ConfigurationSection parameters) {
        super.prepare(context, parameters);
        if (parameters == null) {
            return;
        }

        String lockParam = resolveParameter(parameters, PARAM_LOCK);
        lock = lockParam == null || parameters.getBoolean(lockParam, true);

        String keyParam = resolveParameter(parameters, PARAM_KEY);
        key = keyParam == null ? null : parameters.getString(keyParam);

        String durationParam = resolveParameter(parameters, PARAM_DURATION);
        int rawDuration = durationParam == null
                ? HoldLockManager.DEFAULT_DURATION_TICKS
                : parameters.getInt(durationParam, HoldLockManager.DEFAULT_DURATION_TICKS);
        durationTicks = HoldLockManager.clampDuration(rawDuration);
        if (durationTicks != rawDuration) {
            warn(context, "duration " + rawDuration + " tick は範囲外です。"
                    + durationTicks + " tick に丸めました(上限 "
                    + HoldLockManager.MAX_DURATION_TICKS + " tick)。");
        }
    }

    @Override
    public SpellResult perform(CastContext context) {
        if (context == null) {
            return SpellResult.FAIL;
        }
        if (key == null || key.trim().isEmpty()) {
            warn(context, "key が指定されていません。持ち替え禁止は掛かりません。");
            return SpellResult.FAIL;
        }

        Entity target = resolveTarget(context);
        if (target == null) {
            return SpellResult.NO_TARGET;
        }
        if (!(target instanceof Player player)) {
            return SpellResult.PLAYER_REQUIRED;
        }

        if (lock) {
            HoldLockManager.get().lock(player.getUniqueId(), key, durationTicks);
        } else {
            HoldLockManager.get().unlock(player.getUniqueId(), key);
        }
        return SpellResult.CAST;
    }

    /**
     * 掛ける相手を決める。
     *
     * <p>ターゲットを取れなかったときに詠唱者へフォールバックすると、
     * 「相手に掛けるはずが外れて自分がロックされる」という事故になる。
     * そのため {@code target: none} のとき<b>だけ</b>詠唱者にフォールバックする。
     */
    @Nullable
    private Entity resolveTarget(@NotNull CastContext context) {
        Entity target = context.getTargetEntity();
        if (target == null && context.getTargetType() == TargetType.NONE) {
            target = context.getEntity();
        }
        return target;
    }

    /**
     * 大文字小文字を無視してパラメータ名を引く。
     *
     * @return 実際に yml に書かれているキー。無ければ null
     */
    @Nullable
    private static String resolveParameter(@NotNull ConfigurationSection parameters,@NotNull String name) {
        if (parameters.contains(name)) {
            return name;
        }
        for (String candidate : parameters.getKeys(false)) {
            if (candidate.equalsIgnoreCase(name)) {
                return candidate;
            }
        }
        return null;
    }

    private void warn(@Nullable CastContext context, @NotNull String message) {
        if (warned || context == null || context.getPlugin() == null) {
            return;
        }
        warned = true;
        String spellName = context.getSpell() == null ? "?" : context.getSpell().getKey();
        context.getPlugin().getLogger().warning("HoldLock (" + spellName + "): " + message);
    }
}
