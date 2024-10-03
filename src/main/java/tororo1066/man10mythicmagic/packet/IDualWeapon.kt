package tororo1066.man10mythicmagic.packet

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface IDualWeapon {

    fun sendPacket(p: Player, itemStack: ItemStack)

    fun sendResetPacket(p: Player)

    fun listenPacket()
}