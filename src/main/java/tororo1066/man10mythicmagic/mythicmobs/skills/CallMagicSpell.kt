package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.command.CommandSender
import tororo1066.man10mythicmagic.Man10MythicMagic
import java.io.File

class CallMagicSpell(config : MythicLineConfig, file: File) : SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), ITargetedEntitySkill {
    private val spell : String

    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): SkillResult {
        return if (Man10MythicMagic.magicAPI.cast(spell, arrayOf(), target.bukkitEntity as CommandSender,target.bukkitEntity)){
            SkillResult.SUCCESS
        } else {
            SkillResult.ERROR
        }
    }

    init {
        spell = config.getString(arrayOf("spell","s"))
        isAsyncSafe = false
    }

}