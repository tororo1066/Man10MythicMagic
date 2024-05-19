package tororo1066.man10mythicmagic.command

import com.ticxo.playeranimator.api.PlayerAnimator
import org.bukkit.Bukkit
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.magic.actions.PlayerAnimation
import tororo1066.tororopluginapi.SDebug
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.mysql.SMySQL
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandObject

class MMMCommands : SCommand("mythicmagic","","mmm.op") {

    init {
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("reload")).setNormalExecutor {
            Man10MythicMagic.plugin.reloadConfig()
            SJavaPlugin.mysql = SMySQL(Man10MythicMagic.plugin)
            UsefulUtility.sTry({PlayerAnimator.api.animationManager.clearRegistry()
                PlayerAnimator.api.animationManager.importPacks()}, {})
            Man10MythicMagic.plugin.reload()

            it.sender.sendMessage("§aReloaded")
        })

        registerDebugCommand("mmm.op")
    }
}