package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult

class RemoveEnchantmentEffect : CompoundAction() {

    override fun perform(context: CastContext?): SpellResult {
        context?:return SpellResult.FAIL
        (context.wand?:return SpellResult.FAIL).enchantments = hashMapOf()
        return SpellResult.CAST
    }
}