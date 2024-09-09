package tororo1066.man10mythicmagic.command

import com.ticxo.playeranimator.api.PlayerAnimator
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.otherUtils.UsefulUtility

object ReloadPlayerAnimator {
    fun run() {
        UsefulUtility.sTry({
            PlayerAnimator.api.animationManager.clearRegistry()
            PlayerAnimator.api.animationManager.importPacks()}, {})
    }
}