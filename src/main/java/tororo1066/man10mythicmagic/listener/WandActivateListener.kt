package tororo1066.man10mythicmagic.listener

import com.elmakers.mine.bukkit.api.event.PreCastEvent
import com.elmakers.mine.bukkit.api.event.WandActivatedEvent
import com.elmakers.mine.bukkit.api.wand.WandAction
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.annotation.SEventHandler
import tororo1066.tororopluginapi.sEvent.SEvent

class WandActivateListener {

    init {
        SEvent(Man10MythicMagic.plugin).register(PreCastEvent::class.java){ e ->
            val group = e.mage.activeWand?.template?.getString("group")?:return@register
            if (!Man10MythicMagic.groups.containsKey(group))return@register
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
        SEvent(Man10MythicMagic.plugin).register(WandActivatedEvent::class.java){ e ->
            val template = e.wand.template?:return@register
            val onHold = template.getString("on_hold")?:return@register
            e.wand.performAction(WandAction.valueOf(onHold))
        }
    }
}