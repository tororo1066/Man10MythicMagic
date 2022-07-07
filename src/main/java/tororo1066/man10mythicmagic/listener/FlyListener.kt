package tororo1066.man10mythicmagic.listener

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerToggleFlightEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEvent

class FlyListener {

    init {
        SEvent(Man10MythicMagic.plugin).register(PlayerToggleFlightEvent::class.java) { e ->
            if (!e.isFlying)return@register
            val mage = Man10MythicMagic.magicAPI.controller.getRegisteredMage(e.player)?:return@register
            mage.trigger("fly")
        }

    }
}