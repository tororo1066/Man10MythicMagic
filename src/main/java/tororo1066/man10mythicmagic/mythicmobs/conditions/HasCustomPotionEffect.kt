package tororo1066.man10mythicmagic.mythicmobs.conditions

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.conditions.IEntityCondition
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble
import io.lumine.mythic.core.skills.SkillCondition
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.magic.effect.CustomPotionManager

class HasCustomPotionEffect(config: MythicLineConfig) : SkillCondition(config.line), IEntityCondition {

    val effect: String? = config.getString(arrayOf("effect", "e", "type", "t"), null)
    val amplifier: RangedDouble? = config.getString(arrayOf("amplifier", "a", "level", "l"), null)?.let { RangedDouble(it) }
    val duration: RangedDouble? = config.getString(arrayOf("duration", "d"), null)?.let { RangedDouble(it) }

    override fun check(entity: AbstractEntity): Boolean {
        if (!Man10MythicMagic.foundMagic) return false
        CustomPotionManager.customPotionEffectInstances[entity.uniqueId]?.get(effect)?.forEach {
            val amplifier = this.amplifier
            val duration = this.duration
            if (amplifier == null || amplifier.equals(it.amplifier) && (duration == null || duration.equals(it.duration))) {
                return true
            }
        }
        return false
    }
}