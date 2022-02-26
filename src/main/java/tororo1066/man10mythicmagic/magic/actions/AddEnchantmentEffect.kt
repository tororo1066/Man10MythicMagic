package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment

class AddEnchantmentEffect : CompoundAction() {

    override fun start(context: CastContext?): SpellResult {
        context?:return SpellResult.FAIL
        (context.wand?:return SpellResult.FAIL).addEnchantments(mutableMapOf(Pair(Enchantment.LUCK,1)))
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext?, parameters: ConfigurationSection?) {
        super.prepare(context, parameters)
    }
}