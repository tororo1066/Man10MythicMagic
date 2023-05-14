package tororo1066.man10mythicmagic.listener

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.utils.getAllUsingWands
import tororo1066.tororopluginapi.sEvent.SEvent

class DamageListener {

    init {
        SEvent(Man10MythicMagic.plugin).register(EntityDamageEvent::class.java){ e ->
            if (e.entity !is Player)return@register
            if (e.cause == EntityDamageEvent.DamageCause.FALL){
                if (Man10MythicMagic.magicAPI.controller.getRegisteredMage(e.entity) == null){
                    return@register
                }
                for (wand in getAllUsingWands(e.entity as Player)){
                    if (wand.template?.getBoolean("protect_fall") == true){
                        e.isCancelled = true
                        break
                    }
                }
            }
        }
    }
}