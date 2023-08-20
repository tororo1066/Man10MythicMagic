package tororo1066.man10mythicmagic.noused

import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEventInterface
import java.util.UUID

class MovementListener: SEventInterface<PlayerMoveEvent>(Man10MythicMagic.plugin, PlayerMoveEvent::class.java) {

    companion object {
        val playersVelocity = HashMap<UUID, Vector>()
    }

    override fun executeEvent(e: PlayerMoveEvent) {
        val diff = e.to.toVector().subtract(e.from.toVector())

        if (diff.lengthSquared() > 0.0) {
            playersVelocity[e.player.uniqueId] = diff
        }
    }
}