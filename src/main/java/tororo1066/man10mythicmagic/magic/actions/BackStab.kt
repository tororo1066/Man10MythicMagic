package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext

class BackStab : CheckAction() {

    override fun isAllowed(context: CastContext): Boolean {
        val targetLocation = (context.targetEntity?: return false).location
        val casterLocation = context.mage.location

        return targetLocation.direction.dot(casterLocation.direction) > 0.0
    }
}