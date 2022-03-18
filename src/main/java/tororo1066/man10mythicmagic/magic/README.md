### BlockWave
MythicMobsのblockwaveをmagicで出すクラス\
**パラメーター**\
・material：ブロック\
・radius：半径\
・velocity：勢い\
・duration：時間(tick)\
**対象**\
・実行者の位置\
**例**
```yaml
- class: BlockWave
  material: DIRT
  radius: 5
  velocity: 3.0
  duration: 40
```

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