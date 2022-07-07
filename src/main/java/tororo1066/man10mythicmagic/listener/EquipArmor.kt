package tororo1066.man10mythicmagic.listener

import com.codingforcookies.armorequip.ArmorEquipEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEvent

class EquipArmor {

    init {
        SEvent(Man10MythicMagic.plugin).register(ArmorEquipEvent::class.java) { e ->

            val isEquip: Boolean = if (e.oldArmorPiece != null && e.newArmorPiece != null){
                true
            } else {
                e.oldArmorPiece == null
            }

            if (isEquip){
                val p = e.player
                val wand = Man10MythicMagic.magicAPI.controller.getWand(e.newArmorPiece)?:return@register
                if (wand.template?.getBoolean("fly_on_equip",false) == true){
                    p.allowFlight = true
                    return@register
                }
            } else {
                val p = e.player
                val wand = Man10MythicMagic.magicAPI.controller.getWand(e.oldArmorPiece)?:return@register
                if (wand.template?.getBoolean("unfly_on_unequip",false) == true){
                    p.isFlying = false
                    p.allowFlight = false
                    return@register
                }
            }


        }
    }
}