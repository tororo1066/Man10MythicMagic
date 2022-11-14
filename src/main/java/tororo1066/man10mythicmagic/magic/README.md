### TeleportAndEffect
指定した距離分テレポートしてエフェクトを発生させる\
近くにいたプレイヤーに対してactionを実行できる\
**パラメーター**\
・length：進む距離\
・particles：パーティクル(list可)
・yoffset：パーティクルのoffset\
・radius：actionの半径\
・actions：radiusにいるプレイヤーへのaction\
**対象**\
・エンティティ\
**例**
```yaml
- class: TeleportAndEffect
  length: 10
  particles:
    - SWEEP_ATTACK
  yoffset: 0.8
  radius: 2
  actions:
    - class: Damage
```

### AddEnchantEffect
武器にエンチャントを付与する\
**パラメーター**\
なし\
**対象**\
・プレイヤー(手に杖を持っている場合)\
**例**
```yaml
- class: AddEnchantEffect
```

### RemoveEnchantEffect
武器のエンチャントを削除する\
**パラメーター**\
なし\
**対象**\
・プレイヤー(手に杖を持っている場合)\
**例**
```yaml
- class: RemoveEnchantEffect
```

### CheckEnchantEffect
武器にエンチャントがついているか確認する\
**パラメーター**\
actions：ついている時に実行するaction\
fail：ついていなかった時に実行するaction\
**対象**\
・プレイヤー(手に杖を持っている場合)\
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
**パラメーター**\
material：弾の素材\
name：アイテム名\
cmd：カスタムモデルデータ\
amount：消費量\
ammoPlugin：AmmoPluginを使うか\
actions：消費できた時に実行するaction\
fail：消費できなかった時に実行するaction\
**対象**\
・プレイヤー\
**例**
```yaml
- class: AmmoReload
  material: DIAMOND
  name: test
  cmd: 2
  amount: 10
  ammoPlugin: true
  actions:
    - class: Command
      command: say success
  fail:
    - class: Command
      command: say failed
```

### ThrowItemPlus
アイテムにvelocityを与えて飛ばす\
**パラメーター**\
material：出すアイテム\
cmd：カスタムモデルデータ\
enchant：アイテムにエンチャントをするかどうか\
canPick: 拾えるかどうか\
time: アイテムが消えるまでの時間(tick)\
yVelocity：yのベクトル\
multiply：距離の倍率\
rotate：アイテムが投げられる範囲の角度\
actions：アイテムを拾ったときに実行するaction(canPickがtrueじゃないといけない)\
**対象**\
・エンティティ\
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
**パラメーター**\
skill：スキル名\
**対象**\
・エンティティ\
**例**
```yaml
- class: CastMythicSkill
  skill: DemonJump
```

### CheckCMD
slotにあるアイテムのcmdがあっているか確認する\
**パラメーター**\
cmd：カスタムモデルデータ
slot：スロット(hand,off_hand,head,chest,legs,feet)\
actions：あっている時に実行するaction\
fail：あっていなかった時に実行するaction\
**対象**\
・プレイヤー(手に杖を持っている場合)\
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
**パラメーター**\
min：最小値\
max：最大値\
**対象**\
・プレイヤー(手に杖を持っている場合)\
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

### ThrowArmorStand
アーマースタンドを地面に着地させるまで動かす\
-ArmorStandProjectileでいいので使わなくていいです-

### ReHold
attributeを手に持ち直さずに更新する\
**パラメーター**：なし\
**対象**\
・プレイヤー(手に杖を持っている場合)\
**例**
```yaml
- class: ReHold
```

### LowHealthDmg
casterのHPが少ないほどtargetにダメージを与える\
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
**パラメーター**\
duration：延焼時間(1000で1秒)\
**対象**\
・エンティティ\
**例**
```yaml
- class: IgnitePlus
  duration: 3000
```

### CircleParticle
円のパーティクルを出す\
-正直EffectLibに組み込みたい-\
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

### IsEquipWand
wandを装備しているか確認する\
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
**パラメーター**\
level: ズームの強さ 1~10ぐらいが適切？\
onScope: スコープしたときに変えるcmd\
onUnScope: スコープを解除したときに変えるcmd\
**対象**\
・プレイヤー\
**例**
```yaml
- class: Scope
  level: 3
  onScope: 1
  onUnScope: 2
```

### ScopingAction
プレイヤーにScope状態に応じたActionを実行させる\
**パラメーター**\
actions: Scopeしていたときに実行するaction\
fail: Scopeしていないときに実行するaction\
**対象**\
・プレイヤー\
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
**パラメーター**\
全て-180~180までで指定する\
yaw.min: 横軸の揺れの最低値を指定する\
yaw.max: 横軸の揺れの最大値を指定する\
pitch.min: 縦軸の揺れの最低値を指定する\
pitch.max: 縦軸の揺れの最大値を指定する\
**対象**\
・プレイヤー\
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
**パラメーター**\
damage: ダメージ\
**対象**\
・エンティティ\
**例**
```yaml
- class: IgnoreDamage
  damage: 15
```