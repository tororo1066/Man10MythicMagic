package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tororo1066.man10mythicmagic.Man10MythicMagic

class CallMagicSpell(config : MythicLineConfig) : SkillMechanic(config.line,config), ITargetedEntitySkill {
    private val spell : String

    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): Boolean {
        return Man10MythicMagic.magicAPI.cast(spell, arrayOf(), target.bukkitEntity as CommandSender,target.bukkitEntity)
    }

    init {
        spell = config.getString(arrayOf("spell","s"))
        isAsyncSafe = false
    }

}