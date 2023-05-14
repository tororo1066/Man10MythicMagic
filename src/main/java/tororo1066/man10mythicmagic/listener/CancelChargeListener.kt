package tororo1066.man10mythicmagic.listener

import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.CrossbowMeta
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEvent

class CancelChargeListener {

    init {
        SEvent(Man10MythicMagic.plugin).register(PlayerInteractEvent::class.java){ e ->
            if (!e.action.isRightClick)return@register
            val item = e.item?:return@register
            val wand = Man10MythicMagic.magicAPI.controller.getIfWand(item)?:return@register
            if (item.itemMeta !is CrossbowMeta)return@register
            if (wand.template?.getBoolean("cancel_charge") == true){
                e.isCancelled = true
            }
        }
    }
}