package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.listener.PreCastListener
import java.io.File

class DisableMagicSpells(config : MythicLineConfig, file: File?) : SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), ITargetedEntitySkill {
    val duration: PlaceholderInt = config.getPlaceholderInteger(arrayOf("duration", "d"), 10)
    var add: Boolean = config.getBoolean(arrayOf("add", "a"), false)

    override fun castAtEntity(data: SkillMetadata, entity: AbstractEntity): SkillResult? {
        if (!Man10MythicMagic.foundMagic) return SkillResult.MISSING_COMPATIBILITY
        val bukkitEntity = entity.bukkitEntity
        if (bukkitEntity !is Player) return SkillResult.INVALID_TARGET
        val current = PreCastListener.disabledPlayers[bukkitEntity.uniqueId]
        val duration = duration.get(data).toLong()
        val addDuration = if (add && current != null) {
            current + duration
        } else {
            duration
        }
        PreCastListener.disabledPlayers[bukkitEntity.uniqueId] = addDuration

        return SkillResult.SUCCESS
    }
}