### TeleportAndEffect
指定した距離分テレポートしてエフェクトを発生させる\
近くにいたプレイヤーに対してactionを実行できる\
**パラメーター**
- length：進む距離
- step: 1マスごとに行うaction
- particles：パーティクル(list可) 非推奨
- yoffset：パーティクルのoffset 非推奨
- radius：actionの半径
- actions：radiusにいるプレイヤーへのaction
- relative: 勢いがロックされないようにするか

**対象**
- エンティティ

**例**
```yaml
- class: TeleportAndEffect
  length: 10
  step:
    - class: PlayEffects
      effects: test
  particles:
    - SWEEP_ATTACK
  yoffset: 0.8
  radius: 2
  relative: true
  actions:
    - class: Damage
```

### AddEnchantEffect
武器にエンチャントを付与する\
**パラメーター**\
なし\
**対象**
- プレイヤー(手に杖を持っている場合)

**例**
```yaml
- class: AddEnchantEffect
```

### RemoveEnchantEffect
武器のエンチャントを削除する\
**パラメーター**
- なし

**対象**
- プレイヤー(手に杖を持っている場合)

**例**
```yaml
- class: RemoveEnchantEffect
```

### CheckEnchantEffect
武器にエンチャントがついているか確認する\
**パラメーター**
- actions：ついている時に実行するaction
- fail：ついていなかった時に実行するaction

**対象**
- プレイヤー(手に杖を持っている場合)

**例**
```yaml
- class: CheckEnchantEffect
  actions:
    - class: RemoveEnchantEffect
  fail:
    - class: AddEnchantEffect
```

### AmmoReload
アイテムを消費してactionを実行する\
**パラメーター**
- material：弾の素材
- name：アイテム名
- cmd：カスタムモデルデータ
- amount：消費量
- actions：消費できた時に実行するaction
- fail：消費できなかった時に実行するaction

**対象**
- プレイヤー

**例**
```yaml
- class: AmmoReload
  material: DIAMOND
  name: test
  cmd: 2
  amount: 10
  actions:
    - class: Command
      command: say success
  fail:
    - class: Command
      command: say failed
```

### ThrowItemPlus
アイテムにvelocityを与えて飛ばす\
**パラメーター**
- material：出すアイテム
- cmd：カスタムモデルデータ
- enchant：アイテムにエンチャントをするかどうか
- canPick: 拾えるかどうか
- time: アイテムが消えるまでの時間(tick)
- yVelocity：yのベクトル
- multiply：距離の倍率
- rotate：アイテムが投げられる範囲の角度
- actions：アイテムを拾ったときに実行するaction(canPickがtrueじゃないといけない)

**対象**
- エンティティ

**例**
```yaml
- class: ThrowItemPlus
  material: DIAMOND
  cmd: 2
  enchant: true
  canPick: true
  time: 100
  yVelocity: 0.8
  multiply: 0.5
  rotate: 30
  actions:
    - class: Command
      console: true
      command: tell @t ﾋﾛﾜﾚﾁｬｯﾀ!
```

### CastMythicSkill
Casterに対してMythicMobsのSkillを実行する\
**パラメーター**
- skill：スキル名

**対象**
- エンティティ

**例**
```yaml
- class: CastMythicSkill
  skill: DemonJump
```

### CheckCMD
slotにあるアイテムのcmdがあっているか確認する\
**パラメーター**
- cmd：カスタムモデルデータ
- slot：スロット(hand,off_hand,head,chest,legs,feet)
- actions：あっている時に実行するaction
- fail：あっていなかった時に実行するaction

**対象**
- プレイヤー(手に杖を持っている場合)

**例**
```yaml
- class: CheckCMD
  cmd: 10
  slot: hand
  actions:
    - class: ...
  fail:
    - class: ...
```

### CheckDurability
wandの耐久をチェックする\
**パラメーター**
- min：最小値
- max：最大値

**対象**
- プレイヤー(手に杖を持っている場合)

**例**
```yaml
- class: CheckDurability
  min: 10
  max: 200
  actions:
    - class: ...
  fail:
    - class: ...
```

### ~~ThrowArmorStand~~ -Deprecated-
アーマースタンドを地面に着地させるまで動かす\
-ArmorStandProjectileでいいので使わなくていいです-

### ReHold
attributeを手に持ち直さずに更新する\
**パラメーター**
- なし

**対象**
- プレイヤー(手に杖を持っている場合)

**例**
```yaml
- class: ReHold
```

### ~~LowHealthDmg~~ -Deprecated-
casterのHPが少ないほどtargetにダメージを与える\
($maxHealth-$health)*<数字>で代用できるので非推奨\
計算式 (最大HP-HP)*multiply\
**パラメーター**\
multiply：ダメージの倍率\
**対象**\
・エンティティ(casterはプレイヤーの必要がある)\
**例**
```yaml
- class: LowHealthDmg
  multiply: 1.5
```

### IgnitePlus
通常のIgniteだと額縁が壊れてしまうので作成\
**パラメーター**
- duration：延焼時間(1000で1秒)

**対象**
- エンティティ

**例**
```yaml
- class: IgnitePlus
  duration: 3000
```

### ~~CircleParticle~~ -Deprecated-
円のパーティクルを出す\
EffectLibのWarpで代用できるので非推奨\
**パラメーター**\
radius：半径\
count：パーティクルの量\
points：パーティクルの量(合計)\
particle：パーティクル\
color：パーティクルの色(一部にだけ適応できる)\
offset：パーティクルのオフセット\
**対象**\
・ターゲット\
**例**
```yaml
- class: CirclePaticle
  particle: redstone
  points: 10
  count: 2
  radius: 3
  color: "#000000"
  offset: 1,1,1
```

### ~~IsEquipWand~~ -Deprecated-
wandを装備しているか確認する\
CheckRequirementsで代用できるので非推奨\
**パラメーター**\
slot: 確認するスロット\
wandName: wandの名前\
**対象**\
・プレイヤー\
**例**
```yaml
- class: IsEquipWand
  slot: hand
  wandName: water_wand
  actions:
    - class: ...
  fail:
    - class: ...
```

### Scope
wandのcmd(CustomModelData)を切り替えて覗かせる\
**パラメーター**
- level: ズームの強さ 1~10ぐらいが適切？
- onScope: スコープしたときに変えるcmd
- onUnScope: スコープを解除したときに変えるcmd

**対象**
- プレイヤー

**例**
```yaml
- class: Scope
  level: 3
  onScope: 1
  onUnScope: 2
```

### ScopingAction
プレイヤーにScope状態に応じたActionを実行させる\
**パラメーター**
- actions: Scopeしていたときに実行するaction
- fail: Scopeしていないときに実行するaction

**対象**
- プレイヤー

**例**
```yaml
- class: ScopingAction
  actions:
    - class: ...
  fail:
    - class: ...
```

### Recoil
プレイヤーにcspのような視点のブレを実装させる\
**パラメーター**
全て-180~180までで指定する
- yaw.min: 横軸の揺れの最低値を指定する
- yaw.max: 横軸の揺れの最大値を指定する
- pitch.min: 縦軸の揺れの最低値を指定する
- pitch.max: 縦軸の揺れの最大値を指定する

**対象**
- プレイヤー

**例**
```yaml
- class: Recoil
  yaw:
    min: -10.0
    max: 10.0
  pitch:
    min: 5.0
    max: 5.0
```

### IgnoreDamage
防具完全貫通のダメージを与える\
**パラメーター**
- damage: ダメージ

**対象**
- エンティティ

**例**
```yaml
- class: IgnoreDamage
  damage: 15
```

### IsOnGround
エンティティが地面にいるか確認する\
**パラメーター**
- actions: 地面にいる時に実行するaction
- fail: 地面にいない時に実行するaction

**対象**
- エンティティ

**例**
```yaml
- class: IsOnGround
  actions:
    - class: ...
  fail:
    - class: ...
```

### RecallBackFuture
プレイヤーを過去にいた場所に戻す\
15秒まで遡れる\
**パラメーター**
- time: 遡る時間(tick)
- relative: 勢いがロックされないようにするか

**対象**
- プレイヤー

**例**
```yaml
- class: RecallBackFuture
  time: 200
  relative: true
```

### ArmorStandEquip -Deprecated?-
アーマースタンドに装備させる\
**パラメーター**
- slot: 装備するスロット
- item: 装備するアイテム

**対象**
- アーマースタンド

**例**
```yaml
- class: ArmorStandEquip
  slot: head
  item: iron_sword{CustomModelData:1}
```

### CheckPvPAllowed
PvPが許可されているか確認する\
EntityProjectileなど保護を無視するactionを実行するときに使う\
**パラメーター**
- actions: 許可されている時に実行するaction
- fail: 許可されていない時に実行するaction

**対象**
- プレイヤー

**例**
```yaml
- class: CheckPvPAllowed
  actions:
    - class: ...
  fail:
    - class: ...
```

### FakeItem
偽物のアイテムをプレイヤーに装備させる\
**パラメーター**
- slot: 装備するスロット
- item: 装備するアイテム

**対象**
- プレイヤー

**例**
```yaml
- class: FakeItem
  slot: hand
  item: iron_sword{CustomModelData:1}
```

### BackStab
CasterがTargetの背後にいるか確認する\
**パラメーター**
- actions: 背後にいる時に実行するaction
- fail: 背後にいない時に実行するaction

**対象**
- エンティティ

**例**
```yaml
- class: BackStab
  actions:
    - class: ...
  fail:
    - class: ...
```

### ModifyPropertiesPlus
ModifyPropertiesの拡張版\
値にAttributeやVariableが使えるようになる\
**パラメーター**\
省略

**対象**\
省略

**例**
```yaml
- class: ModifyPropertiesPlus
  modify_target: wand
  modify:
    - property: name
      value: "@test_attribute@ wand
```

### UniqueVariable
Targetに対して一意な変数を設定する\
**パラメーター**\
- type: (store: 変数を代入する, restore: 変数を取得する, clear: 変数を削除する)
- variable: 変数名
- value: 変数に代入する値(storeの時のみ)
- restore_name: 取得した変数の代入先(restoreの時のみ)

**対象**\
- エンティティ

**例**
```yaml
- class: UniqueVariable
  type: restore
  variable: test
  restore_name: test
- class: ModifyVariable
  variable: test
  value: $test + 10
- class: UniqueVariable
  type: store
  variable: test
  value: $test
```