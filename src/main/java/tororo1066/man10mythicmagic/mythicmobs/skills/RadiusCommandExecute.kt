package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.Bukkit
import tororo1066.man10mythicmagic.Man10MythicMagic
import java.io.File

class RadiusCommandExecute(config: MythicLineConfig, file: File?) : SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), ITargetedEntitySkill {

    var onlyPlayer = true
    var radius = 3.0
    var command = ""

    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): SkillResult {
        if (onlyPlayer){
            target.bukkitEntity.location.getNearbyPlayers(radius).forEach {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command.replace("<uuid>",it.uniqueId.toString()).replace("<name>",it.name))
            }
        } else {
            target.bukkitEntity.location.getNearbyLivingEntities(radius).forEach {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command.replace("<uuid>",it.uniqueId.toString()).replace("<name>",it.name))
            }
        }

        return SkillResult.SUCCESS
    }

    init {
        this.isAsyncSafe = false
        this.onlyPlayer = config.getBoolean(arrayOf("onlyplayer","onlyp","op"),true)
        this.radius = config.getDouble(arrayOf("radius","r"),3.0)
        this.command = config.getPlaceholderString(arrayOf("command","c"),"").get()
    }

}