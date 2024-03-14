package tororo1066.man10mythicmagic.magic.trigger

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEvent
import java.util.UUID

object UltimateTrigger {
    val lastDamageEntities = HashMap<UUID, Entity>()

    init {
        SEvent(Man10MythicMagic.plugin).register(EntityDamageByEntityEvent::class.java) { e ->
            if (e.damager is Player) {
                lastDamageEntities[e.damager.uniqueId] = e.entity
            }
        }
    }
}