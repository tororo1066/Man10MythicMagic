package tororo1066.man10mythicmagic.command

import org.bukkit.Bukkit
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.magic.effect.CustomPotionManager
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandObject

class MMMCommands : SCommand(
    "mythicmagic",
    perm = "mmm.op"
) {

    init {
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("reload")).setNormalExecutor {
            Man10MythicMagic.plugin.reloadConfig()
            Man10MythicMagic.plugin.reload()
            if (Bukkit.getPluginManager().getPlugin("Magic") != null){
                CustomPotionManager.load()
            }
            it.sender.sendMessage("§aReloaded")
        })

        addCommand(SCommandObject().addArg(SCommandArg("test")).setPlayerExecutor {
            it.sender.sendMessage(
                CustomPotionManager.customPotionEffectInstances.toString()
            )
        })

        registerDebugCommand("mmm.op")
    }
}