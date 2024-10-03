package tororo1066.man10mythicmagic.packet.latest

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.packet.IDualWeapon

class DualWeapon: IDualWeapon {

    override fun sendPacket(p: Player, itemStack: ItemStack) {
        if (Man10MythicMagic.protocolManager == null) return

        val protocolManager = Man10MythicMagic.protocolManager!!

        val packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT).apply {
            integers.write(0, p.entityId)
            slotStackPairLists.write(0, listOf(Pair(EnumWrappers.ItemSlot.OFFHAND, itemStack)))
        }

        protocolManager.broadcastServerPacket(packet)
    }

    override fun sendResetPacket(p: Player) {
        if (Man10MythicMagic.protocolManager == null) return

        val protocolManager = Man10MythicMagic.protocolManager!!

        val packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT).apply {
            integers.write(0, p.entityId)
            slotStackPairLists.write(0, listOf(Pair(EnumWrappers.ItemSlot.OFFHAND, p.inventory.itemInOffHand)))
        }

        protocolManager.broadcastServerPacket(packet)
    }

    override fun listenPacket() {
        if (Man10MythicMagic.protocolManager == null) return

        val protocolManager = Man10MythicMagic.protocolManager!!

        protocolManager.addPacketListener(object : PacketAdapter(Man10MythicMagic.plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            override fun onPacketSending(event: PacketEvent) {
                val clone = event.packet.deepClone()
                val entity = clone.getEntityModifier(event).read(0)
                val equipments = clone.slotStackPairLists.read(0)
                if (entity == null || equipments.isEmpty()) return

                val offHand = equipments.find { it.first == EnumWrappers.ItemSlot.OFFHAND }?.second ?: return

                val player = entity as? Player ?: return

                if (offHand.isSimilar(player.inventory.itemInMainHand)) return

                val wand = Man10MythicMagic.magicAPI.controller.getIfWand(player.inventory.itemInMainHand) ?: return

                if (wand.template?.getBoolean("dual_weapon") == true) {
                    event.packet.slotStackPairLists.write(0, listOf(Pair(EnumWrappers.ItemSlot.OFFHAND, wand.item)))
                }
            }
        })
    }
}