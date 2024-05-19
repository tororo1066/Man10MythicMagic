### Trigger

triggerを使用するとき
```yaml
triggers:
  - trigger: kill
    ultimate: true
```
ultimate: trueをつけると、triggerに色々な機能追加できる\

### ダメージを与えたエンティティを確認する
**パラメーター**
- damage_entity_type: ダメージを与えたエンティティのタイプ

**利用可能なトリガー**
- kill
- damage_dealt

**例**
```yaml
triggers:
  - trigger: kill
    ultimate: true
    damage_entity_type: player
```

### ダメージを与えたエンティティをtargetとして実行する
**パラメーター**
- get_target: 有効/無効

**利用可能なトリガー**
- kill
- damage_dealt

**例**
```yaml
triggers:
  - trigger: kill
    ultimate: true
    get_target: true
actions:
  cast:
    - class: Message
      message: "Killed by UltimateSword..."
      message_target: true
```