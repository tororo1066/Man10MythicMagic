package tororo1066.man10mythicmagic.listener

import com.elmakers.mine.bukkit.api.event.WandDeactivatedEvent
import tororo1066.man10mythicmagic.magic.actions.Scope
import tororo1066.tororopluginapi.annotation.SEvent

class WandDeactivateListener {

    @SEvent
    fun event(e: WandDeactivatedEvent){
        if (e.mage.player?.hasMetadata("magic_scope") == true){
            Scope.unScopePlayer(e.mage.player!!,e.wand)
        }
    }
}