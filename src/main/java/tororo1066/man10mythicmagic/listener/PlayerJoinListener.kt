package tororo1066.man10mythicmagic.listener

import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.nmsutils.SPlayer
import tororo1066.tororopluginapi.sEvent.SEvent

class PlayerJoinListener {
    init {
        SEvent().register<PlayerJoinEvent> { e ->
            val sPlayer = SPlayer.getSPlayer(e.player)
            sPlayer.initGlowTeam("always")
        }
    }
}