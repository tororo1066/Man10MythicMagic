package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class ArmorStandEquip: CompoundAction() {

    lateinit var item: ItemStack
    lateinit var slot: EquipmentSlot


    override fun perform(context: CastContext): SpellResult {
        val armorStand = context.targetEntity as? ArmorStand?:return SpellResult.NO_TARGET
        armorStand.setItem(slot, item)
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        item = context.controller.createItem(parameters.getString("item"))!!
        slot = EquipmentSlot.valueOf(parameters.getString("slot","hand")!!.uppercase())
    }

}