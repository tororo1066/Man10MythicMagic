package tororo1066.man10mythicmagic.listener

import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.tasks.PlayerLocationTrackTask
import tororo1066.tororopluginapi.sEvent.SEvent

class PlayerLocationTrackListener {

    init {
        SEvent(Man10MythicMagic.plugin).register(PlayerJoinEvent::class.java) { e ->
            PlayerLocationTrackTask.players[e.player.uniqueId] = PlayerLocationTrackTask(e.player.uniqueId)
        }
    }
}