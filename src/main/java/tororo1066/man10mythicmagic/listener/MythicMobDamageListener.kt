package tororo1066.man10mythicmagic.listener

import io.lumine.mythic.api.skills.damage.DamageMetadata
import io.lumine.mythic.bukkit.BukkitAdapter
import org.bukkit.Bukkit
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEvent

class MythicMobDamageListener {

    init {
        SEvent(Man10MythicMagic.plugin).register(EntityDamageByEntityEvent::class.java, EventPriority.LOWEST) { e ->
            if (e.isCancelled) return@register
            val target = e.entity
            val activeMob = Man10MythicMagic.mythicMobs.mobManager.getMythicMobInstance(target) ?: return@register

            val element = target.getMetadata("element").find {
                it.owningPlugin == Man10MythicMagic.plugin
            }?.asString() ?: return@register

            val damageMetadata = DamageMetadata(
                Man10MythicMagic.mythicMobs.skillManager.getCaster(BukkitAdapter.adapt(e.damager)),
                e.damage,
                mapOf(),
                mapOf(),
                element,
                1.0,
                null,
                null,
                null,
                null,
                e.cause
            )

            damageMetadata.putTag(element)

            activeMob.entity.setMetadata("skill-damage", damageMetadata)

            Bukkit.getScheduler().runTask(Man10MythicMagic.plugin, Runnable {
                activeMob.entity.removeMetadata("skill-damage")
            })
        }
    }
}