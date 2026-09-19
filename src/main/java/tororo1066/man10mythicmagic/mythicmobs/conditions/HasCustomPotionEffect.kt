package tororo1066.man10mythicmagic.mythicmobs.conditions

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble
import io.lumine.mythic.core.skills.SkillCondition
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.magic.effect.CustomPotionManager

class HasCustomPotionEffect(config: MythicLineConfig) : SkillCondition(config.line), IEntityComparisonCondition {

    val effect: String? = config.getString(arrayOf("effect", "e", "type", "t"), null)
    val amplifier: RangedDouble? = config.getString(arrayOf("amplifier", "a", "level", "l"), null)?.let { RangedDouble(it) }
    val duration: RangedDouble? = config.getString(arrayOf("duration", "d"), null)?.let { RangedDouble(it) }
    val private: Boolean = config.getBoolean(arrayOf("private", "p"), false)

    override fun check(caster: AbstractEntity, target: AbstractEntity): Boolean {
        if (!Man10MythicMagic.foundMagic) return false
        CustomPotionManager.customPotionEffectInstances[target.uniqueId]?.get(effect)?.forEach {
            val amplifier = this.amplifier
            val duration = this.duration
            if ((!private && it.grantor != null) || (private && it.grantor != caster.uniqueId)) return@forEach
            if (amplifier == null || amplifier.equals(it.amplifier) && (duration == null || duration.equals(it.duration))) {
                return true
            }
        }
        return false
    }
}