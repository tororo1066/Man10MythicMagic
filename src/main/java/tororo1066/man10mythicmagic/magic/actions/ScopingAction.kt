package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection

class ScopingAction: CompoundAction() {

    override fun addHandlers(spell: Spell?, parameters: ConfigurationSection?) {
        addHandler(spell,"fail")
        addHandler(spell,"actions")
    }

    override fun perform(context: CastContext): SpellResult {
        val p = (context.mage.player)?:return SpellResult.FAIL
        if (p.hasMetadata("magic_scope")){
            getHandler("actions")?.cast(context,context.workingParameters)
        } else {
            getHandler("fail")?.cast(context,context.workingParameters)
        }
        return SpellResult.CAST
    }
}