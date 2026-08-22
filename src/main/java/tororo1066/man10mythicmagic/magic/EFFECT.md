# CustomPotionEffect
カスタムした効果を作ることができます

`plugins/Man10MythicMagic/effects/`にファイルを作成してください
```yaml
<name>:
  tick_interval: 1 # 何tickごとにtickActionを実行するか デフォルトは1
  player_specific: false # 付与者ごとに効果を持たせるか デフォルトはfalse
  cast_remove_on_override: false # 上位の効果を付与されたときにremoveActionを実行するか デフォルトはfalse
  remove_on_death: true # 死亡時に効果を削除するか デフォルトはtrue
  display:
    enabled: true # CustomHudの一覧に表示するか デフォルトはtrue
    icon: E210 # CustomHudリソースパックで定義したアイコンのUnicode code point（16進数）
    # icon-frames を指定すると、先頭を通常時のアイコンとして使用する
    # 残り時間が pulse-before-ticks 以下になると、明 → 暗 → 明 と往復する
    icon-frames: [E210, E211, E212, E213]
    pulse-before-ticks: 100
    pulse-interval-ticks: 3
    priority: 0 # 大きいものから先に表示
  actions:
    add: # 効果を付与するときに実行するaction
      - class: ...
    tick: # tickごとに実行するaction
      - class: ...
    remove: # 効果を削除するときに実行するaction
      - class: ...
```
変数として
- $duration: 効果の残り時間
- $amplifier: 効果のレベル

が使えます\
player_specificがtrueの場合ターゲットが付与者になります

CustomHud が有効な場合、プレイヤーごとの `CustomPotionEffectRenderer` が有効な効果をアイコンで描画します。各効果の `display.icon` には CustomHud リソースパックで定義したアイコンの Unicode code point を16進数で指定してください（例: `E210` または `U+E210`）。アイコンを指定しなかった効果は `config.yml` の `custom-potion-effect-hud.default-icon` を使い、こちらも空欄なら表示しません。各行のフォントは `row-font-pattern` の `%d` を表示順で置換して選択します。

`display.icon-frames` は明るい順から暗い順に並べたアイコンの code point です。`pulse-before-ticks` を正の値にすると、残り時間がその値以下になった時点でフレームを明 → 暗 → 明の順に往復させます。切替間隔は `pulse-interval-ticks`（1以上、既定値3）です。`icon-frames` を指定した場合は通常時に先頭フレームを表示します。

/mythicmagic reloadで適用されます

# Actions

## private
これをtrueにすると付与者を指定します\
付与者が指定されているものはその付与者がcasterでないと情報を取得できません\
自分だけに効果があるエフェクトを作るときに使います

## CustomPotionEffect
カスタムした効果を付与または削除する\
**パラメーター**
- add_effects：付与する効果
- remove_effects：削除する効果
- duration：効果の持続時間(ミリ秒)
- private: 付与者を指定するか

**対象**
- エンティティ

**例**
```yaml
- class: CustomPotionEffect
  add_effects:
    custom_effect: 1 # レベル
  duration: 1000
  remove_effects:
    - custom_effect_2
  private: true
```

## CheckCustomPotionEffect
カスタムした効果がついているか確認する\
**パラメーター**
- effects：確認する効果
- actions：ついている時に実行するaction
- fail：ついていなかった時に実行するaction
- private: 付与者を指定するか

**対象**
- エンティティ

**例**
```yaml
- class: CheckCustomPotionEffect
  effects:
    custom_effect: 1 # レベル
  actions:
    - class: ...
  fail:
    - class: ...
  private: true
```
(以下の方法でも指定することができます)
```yaml
effects:
  custom_effect:
    min: <最小値>
    max: <最大値>
```
```yaml
effects:
  custom_effect:
    value: <値>
```

## RestoreCustomPotionEffectInfo
カスタムした効果の情報を変数に代入します\
`$<効果名>_duration`, `$<効果名>_amplifier`で取得できます\
**パラメーター**
- effect: 取得する効果
- private: 付与者を指定するか

**対象**
- エンティティ

**例**
```yaml
- class: RestoreCustomPotionEffectInfo
  effect: custom_effect
  private: true
- class: Message
  message: $custom_effect_duration, $custom_effect_amplifier
```
