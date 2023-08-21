package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext
import tororo1066.man10mythicmagic.Man10MythicMagic

class CheckAllowedPvP: CheckAction() {

    override fun isAllowed(context: CastContext): Boolean {
        return Man10MythicMagic.magicAPI.controller.isPVPAllowed(context.mage.player,context.targetLocation)
    }
}