### Trigger

triggerを使用するとき
```yaml
triggers:
  - trigger: kill
    ultimate: true
```
ultimate: trueをつけると、さらに条件を追加してtriggerを管理できる\

**パラメーター**
- damage_entity_type: ダメージを与えたエンティティのタイプ

**例**
```yaml
triggers:
  - trigger: kill
    ultimate: true
    damage_entity_type: player
```