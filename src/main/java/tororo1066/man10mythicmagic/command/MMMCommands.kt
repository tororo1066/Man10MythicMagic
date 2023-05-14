package tororo1066.man10mythicmagic.command

import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.mysql.SMySQL
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandObject

class MMMCommands : SCommand("mythicmagic","","mmm.op") {

    init {
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("reload")).setNormalExecutor {
            Man10MythicMagic.plugin.reloadConfig()
            SJavaPlugin.mysql = SMySQL(Man10MythicMagic.plugin)
            Man10MythicMagic.plugin.reload()

            it.sender.sendMessage("§aReloaded")
        })
    }
}