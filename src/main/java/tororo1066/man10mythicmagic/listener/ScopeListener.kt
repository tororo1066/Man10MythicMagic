package tororo1066.man10mythicmagic.listener

import com.elmakers.mine.bukkit.api.event.WandDeactivatedEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.magic.actions.Scope
import tororo1066.tororopluginapi.annotation.SEventHandler
import tororo1066.tororopluginapi.sEvent.SEvent

class ScopeListener {

    init {
        SEvent(Man10MythicMagic.plugin).register(WandDeactivatedEvent::class.java) { e ->
            if (e.mage.player?.hasMetadata("magic_scope") == true){
                Scope.unScopePlayer(e.mage.player!!,e.wand)
            }
        }
    }
}