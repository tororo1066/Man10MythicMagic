package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import org.bukkit.Bukkit

class RadiusCommandExecute(config: MythicLineConfig) : SkillMechanic(config.line,config), ITargetedEntitySkill {

    var onlyPlayer = true
    var radius = 3.0
    var command = ""

    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): Boolean {
        if (onlyPlayer){
            target.bukkitEntity.location.getNearbyPlayers(radius).forEach {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command.replace("<uuid>",it.uniqueId.toString()).replace("<name>",it.name))
            }
        } else {
            target.bukkitEntity.location.getNearbyLivingEntities(radius).forEach {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command.replace("<uuid>",it.uniqueId.toString()).replace("<name>",it.name))
            }
        }

        return true
    }

    init {
        this.isAsyncSafe = false
        this.onlyPlayer = config.getBoolean(arrayOf("onlyplayer","onlyp","op"),true)
        this.radius = config.getDouble(arrayOf("radius","r"),3.0)
        this.command = config.getPlaceholderString(arrayOf("command","c"),"").get()
    }

}