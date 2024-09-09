# CustomPotionEffect
カスタムした効果を作ることができます(時間表示はまだ未対応)

`plugins/Man10MythicMagic/effects/`にファイルを作成してください
```yaml
<name>:
  tick_interval: 1 # 何tickごとにtickActionを実行するか デフォルトは1
  player_specific: false # 付与者ごとに効果を持たせるか デフォルトはfalse
  cast_remove_on_override: false # 上位の効果を付与されたときにremoveActionを実行するか デフォルトはfalse
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
$<効果名>_duration, $<効果名>_amplifierで取得できます\
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