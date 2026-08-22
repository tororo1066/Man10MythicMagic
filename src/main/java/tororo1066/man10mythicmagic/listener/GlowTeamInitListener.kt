package tororo1066.man10mythicmagic.listener

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.nmsutils.SPlayer
import tororo1066.tororopluginapi.sEvent.SEvent

class GlowTeamInitListener {
    init {
        val sEvent = SEvent()
        sEvent.register<PlayerJoinEvent> { e ->
            val sPlayer = SPlayer.getSPlayer(e.player)
            sPlayer.initGlowTeam("always")
            Man10MythicMagic.packetListener?.injectPlayer("man10mythicmagic", e.player)
        }

        sEvent.register<PlayerQuitEvent> { e ->
            Man10MythicMagic.packetListener?.removePlayer("man10mythicmagic", e.player)
        }
    }
}