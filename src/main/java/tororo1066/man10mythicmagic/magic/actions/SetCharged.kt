package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta

class SetCharged: CompoundAction() {

    var isCharged = true

    override fun start(context: CastContext): SpellResult {
        val item = context.wand?.item?:return SpellResult.FAIL
        val meta = item.itemMeta
        if (meta !is CrossbowMeta)return SpellResult.FAIL
        if (isCharged){
            meta.setChargedProjectiles(mutableListOf(ItemStack(Material.ARROW)))
        } else {
            meta.setChargedProjectiles(mutableListOf())
        }
        item.itemMeta = meta

        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        isCharged = parameters.getBoolean("isCharged",true)
    }
}