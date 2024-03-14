package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.Man10MythicMagic

class CheckPvPAllowed: CheckAction() {

    override fun isAllowed(context: CastContext): Boolean {
        return context.targetEntity !is Player || Man10MythicMagic.magicAPI.controller.isPVPAllowed(context.mage.player,context.targetLocation)
    }
}