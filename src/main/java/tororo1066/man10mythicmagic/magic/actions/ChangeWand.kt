package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.ActionFactory
import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import tororo1066.man10mythicmagic.Man10MythicMagic

class ChangeWand: CompoundAction() {

    var wand = ""

    override fun perform(context: CastContext): SpellResult {
        val entity = context.targetEntity
        if (entity !is Player)return SpellResult.FAIL
        ActionFactory.construct("RemoveWand").perform(context)
        entity.inventory.setItem(EquipmentSlot.HAND,Man10MythicMagic.magicAPI.createWand(wand).item)
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        wand = parameters.getString("wand","")!!
    }
}