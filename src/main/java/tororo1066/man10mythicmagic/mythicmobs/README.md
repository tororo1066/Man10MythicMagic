### ARMORDAMAGE
防具一式にダメージを与える\
固定値\
**パラメーター**\
・amount、a：与えるダメージ\
**対象**\
・プレイヤー\
**例**
```yaml
Skills:
  - armordamage{a=10} @PIR{r=10}
```

### SETROTATIONPLUS
mobの向きを変更させる\
**パラメーター**\
・yaw、y：横\
・pitch、p：縦\
**対象**\
・エンティティ\
**例**
```yaml
Skills:
  - setrotationplus{yaw=45;pitch=0} @self
```

### SUMMONPLUS
実行主の位置から座標を増減させた位置にモブ(MythicMob)を召喚する\
**パラメーター**\
・mob, m：モブの名前\
・x：増減させるx座標\
・y：増減させるy座標\
・z：増減させるz座標\
**対象**\
・なし\
**例**
```yaml
Skills:
  - summonplus{x=2;z=2;mob=TestMob}
```

### CALLSPELL
Magicのspellを実行する\
**パラメーター**\
・spell, s：スペル名\
**対象**\
・エンティティ\
**例**
```yaml
Skills:
  - callspell{s=beam} @self
```

### RADIUSCOMMAND
指定したエンティティの周りにいるエンティティ(プレイヤー)にコマンドを実行する\
**パラメーター**\
・radius, r：範囲(小数も可)\
・onlyplayer, op：プレイヤーだけ実行する(デフォルトでtrue)\
・command, c：コマンド
```
<uuid> プレイヤーのUUID
<name> プレイヤーの名前
```
**対象**\
・エンティティ\
**例**
```yaml
Skills:
  - radiuscommand{r=10;op=false;c="minecraft:effect clear <name>"}
```

### CUSTOMPOTIONEFFECT
対象にCustomPotionEffectを付与する(要Magic)\
**パラメータ**\
・effect, e, type, t:エフェクト名\
・duration, d:効果時間(tick)\
・amplifier, a, level, l:レベル\
・private:付与者を指定するか\
**対象**\
・エンティティ\
**例**
```yaml
Skills:
  - custompotioneffect{e=infinity_speed;d=100;a=0;private=true} @self
```

### HASCUSTOMPOTIONEFFECT
対象が指定したCustomPotionEffectを所持するかチェックする(Condition)(要Magic)\
MythicMobsの範囲指定等使用可能\
**パラメータ**
・effect, e, type, t:エフェクト名\
・duration, d:効果時間(tick)\
・amplifier, a, level, l:レベル\
・private:付与者を指定するか\
**例**
```yaml
Conditions:
  - hascustompotioneffect{e=infinity_speed;d=100;a=0;private=true}
```