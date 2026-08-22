package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.core.skills.SkillMechanic
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.magic.effect.CustomPotionManager
import java.io.File

class CustomPotionEffectSkill(config: MythicLineConfig, file: File?): SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), ITargetedEntitySkill {
    val effect = config.getPlaceholderString(arrayOf("effect", "e", "type", "t"), null)
    val duration = config.getPlaceholderInteger(arrayOf("duration", "d"), 200)
    val amplifier = config.getPlaceholderInteger(arrayOf("amplifier", "a", "level", "l"), 0)
    val private = config.getPlaceholderBoolean(arrayOf("private", "p"), false)


    override fun castAtEntity(data: SkillMetadata, entity: AbstractEntity): SkillResult {
        if (!Man10MythicMagic.foundMagic) return SkillResult.MISSING_COMPATIBILITY
        val effectString = effect.get(data)
        CustomPotionManager.addPotionEffect(entity.bukkitEntity, effectString, duration.get(data), amplifier.get(data), if (private.get(data)) data.caster.entity.uniqueId else null)
        return SkillResult.SUCCESS
    }
}