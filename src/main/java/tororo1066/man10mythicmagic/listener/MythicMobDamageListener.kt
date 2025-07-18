package tororo1066.man10mythicmagic.listener

import org.bukkit.event.entity.EntityDamageByEntityEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEvent

class MythicMobDamageListener {

    init {
        SEvent(Man10MythicMagic.plugin).register<EntityDamageByEntityEvent> { e ->
            if (e.isCancelled) return@register
            val target = e.entity
            val activeMob = Man10MythicMagic.mythicMobs.mobManager.getMythicMobInstance(target) ?: return@register
            val mythicMob = activeMob.type

            val modifiers = mythicMob.damageModifiers

            val element = target.getMetadata("element").find {
                it.owningPlugin == Man10MythicMagic.plugin
            }?.asString()


            val modifier = modifiers[element] ?: return@register

            e.damage *= modifier

            activeMob.variables.putString("lastElement",element)
        }
    }
}