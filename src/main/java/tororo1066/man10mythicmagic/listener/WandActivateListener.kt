package tororo1066.man10mythicmagic.listener

import com.elmakers.mine.bukkit.api.event.PreCastEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.annotation.SEventHandler

class WandActivateListener {

    @SEventHandler
    fun event(e: PreCastEvent){
        val group = e.mage.activeWand?.template?.getString("group")?:return
        if (!Man10MythicMagic.groups.containsKey(group))return
        val hotbar = (0..8).toList().mapNotNull { e.mage.inventory?.getItem(it) }
        var hotbarCount = 0
        hotbar.forEach {
            val wand = Man10MythicMagic.magicAPI.controller.getIfWand(it)?:return@forEach
            if ((wand.template?.getString("group")?:return@forEach) != group)return@forEach
            hotbarCount++
        }

        if (Man10MythicMagic.groups[group]!! < hotbarCount){
            e.mage.player?.sendMessage("§c${group}類の武器は${Man10MythicMagic.groups[group]}個までしか使えません")
            e.isCancelled = true
        }
    }
}