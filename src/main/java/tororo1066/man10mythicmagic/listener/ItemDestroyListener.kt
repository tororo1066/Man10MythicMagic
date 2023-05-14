package tororo1066.man10mythicmagic.listener

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerItemBreakEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEventInterface

class ItemDestroyListener: SEventInterface<PlayerItemBreakEvent>(Man10MythicMagic.plugin,PlayerItemBreakEvent::class.java) {

    override fun executeEvent(e: PlayerItemBreakEvent) {
        val wand = Man10MythicMagic.magicAPI.controller.getIfWand(e.brokenItem)?:return
        val str = wand.template?.getString("replace_on_break")
        if (str != null){
            val getWand = Man10MythicMagic.magicAPI.createWand(str)?:return
            Bukkit.getScheduler().runTaskLater(Man10MythicMagic.plugin, Runnable {
                e.player.inventory.setItemInMainHand(getWand.item)
            },1)
        }
    }
}