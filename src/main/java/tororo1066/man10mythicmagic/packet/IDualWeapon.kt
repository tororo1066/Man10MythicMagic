package tororo1066.man10mythicmagic.packet

import com.elmakers.mine.bukkit.api.wand.Wand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10mythicmagic.Man10MythicMagic

interface IDualWeapon {

    fun sendPacket(p: Player, wand: Wand)

    fun sendResetPacket(p: Player)

    fun listenPacket()

    companion object {
        fun Wand.isDualWeapon(): Boolean {
            val template = this.template ?: return false
            return template.getBoolean("dual_weapon.enabled") || template.getBoolean("dual_weapon")
        }

        fun Wand.getDualWeaponIcon(): ItemStack? {
            if (!isDualWeapon()) return null
            val template = this.template ?: return null
            val iconString = template.getString("dual_weapon.icon")
            val wandItemStack = this.item
            val itemStack = if (iconString != null) {
                Man10MythicMagic.magicAPI.controller.createItem(iconString) ?: wandItemStack ?: return null
            } else {
                wandItemStack ?: return null
            }

            return itemStack
        }
    }
}