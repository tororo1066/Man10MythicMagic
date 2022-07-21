package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult

class ReHold: CompoundAction() {

    override fun start(context: CastContext): SpellResult {
        context.mage.attributesUpdated()
        return SpellResult.CAST
    }
}