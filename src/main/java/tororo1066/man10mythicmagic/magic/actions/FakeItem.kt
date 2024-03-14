package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import tororo1066.nmsutils.SPlayer

class FakeItem: CompoundAction() {

    lateinit var slot: EquipmentSlot
    lateinit var item: ItemStack

    override fun start(context: CastContext): SpellResult {
        val p = context.mage.player?:return SpellResult.PLAYER_REQUIRED
        SPlayer.getSPlayer(p).setFakeItem(slot, item)
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        slot = EquipmentSlot.valueOf(parameters.getString("slot","hand")!!.uppercase())
        item = context.controller.createItem(parameters.getString("item"))!!
    }
}