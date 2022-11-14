package tororo1066.man10mythicmagic.listener

import com.codingforcookies.armorequip.ArmorEquipEvent
import org.bukkit.event.EventPriority
import org.bukkit.inventory.ItemStack
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEvent

class EquipArmor {

    private fun isAirOrNull(itemStack: ItemStack?): Boolean {
        return itemStack == null || itemStack.type.isAir
    }

    init {
        SEvent(Man10MythicMagic.plugin).register(ArmorEquipEvent::class.java,EventPriority.HIGHEST) { e ->
            val isEquip: Boolean? = if (!isAirOrNull(e.oldArmorPiece) && !isAirOrNull(e.newArmorPiece)){
                null
            } else {
                isAirOrNull(e.oldArmorPiece)
            }

            if (isEquip == null){
                onEquipTask(e)
                onUnEquipTask(e)
            } else {
                if (isEquip){
                    onEquipTask(e)
                } else {
                    onUnEquipTask(e)
                }
            }


        }
    }

    fun onEquipTask(e: ArmorEquipEvent){
        val p = e.player
        val wand = Man10MythicMagic.magicAPI.controller.getIfWand(e.newArmorPiece)?:return
        if (wand.template?.getBoolean("cancel_on_offhand",false) == true){
            if (e.player.inventory.itemInOffHand == e.newArmorPiece){
                e.isCancelled = true
                return
            }
        }

        if (wand.template?.getBoolean("fly_on_equip",false) == true){
            p.allowFlight = true
            return
        }
    }

    fun onUnEquipTask(e: ArmorEquipEvent){
        val p = e.player
        val wand = Man10MythicMagic.magicAPI.controller.getIfWand(e.oldArmorPiece)?:return
        if (wand.template?.getBoolean("unfly_on_unequip",false) == true){
            p.isFlying = false
            p.allowFlight = false
            return
        }
    }
}