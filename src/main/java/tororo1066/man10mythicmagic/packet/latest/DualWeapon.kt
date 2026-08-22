package tororo1066.man10mythicmagic.packet.latest

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import com.elmakers.mine.bukkit.api.wand.Wand
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.packet.IDualWeapon
import tororo1066.man10mythicmagic.packet.IDualWeapon.Companion.getDualWeaponIcon
import tororo1066.man10mythicmagic.packet.IDualWeapon.Companion.isDualWeapon
import tororo1066.man10mythicmagic.packet.VersionHandler

class DualWeapon: IDualWeapon {

    override fun sendPacket(p: Player, wand: Wand) {
        val protocolManager = VersionHandler.protocolManager

        val icon = wand.getDualWeaponIcon() ?: return

        val packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT).apply {
            integers.write(0, p.entityId)
            slotStackPairLists.write(0, listOf(Pair(EnumWrappers.ItemSlot.OFFHAND, icon)))
        }

        protocolManager.broadcastServerPacket(packet)
    }

    override fun sendResetPacket(p: Player) {
        val protocolManager = VersionHandler.protocolManager

        val packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT).apply {
            integers.write(0, p.entityId)
            slotStackPairLists.write(0, listOf(Pair(EnumWrappers.ItemSlot.OFFHAND, p.inventory.itemInOffHand)))
        }

        protocolManager.broadcastServerPacket(packet)
    }

    override fun listenPacket() {
        val protocolManager = VersionHandler.protocolManager

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

                if (wand.isDualWeapon()) {
                    val icon = wand.getDualWeaponIcon() ?: return
                    event.packet.slotStackPairLists.write(0, listOf(Pair(EnumWrappers.ItemSlot.OFFHAND, icon)))
                }
            }
        })
    }
}