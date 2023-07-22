package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.builtin.ArmorStandProjectileAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult

class ArmorStandProjectileR: ArmorStandProjectileAction() {

    override fun step(context: CastContext?): SpellResult {
        return SpellResult.CAST
    }
}