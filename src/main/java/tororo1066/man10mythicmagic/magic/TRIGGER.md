### Trigger


使わないで
\
\
\
\
\
\
\
\
\
\
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