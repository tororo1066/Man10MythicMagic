package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext

class IsOnGround: CheckAction() {
    override fun isAllowed(context: CastContext): Boolean {
        return (context.targetEntity?:return false).isOnGround
    }
}