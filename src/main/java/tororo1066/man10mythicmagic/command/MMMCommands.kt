package tororo1066.man10mythicmagic.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Material
import org.bukkit.util.Vector
import tororo1066.tororopluginapi.sCommand.*
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer

class MMMCommands : SCommand("mythicmagic","","mmm.op") {

    init {
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("velo")).addArg(SCommandArg().addAllowType(SCommandArgType.DOUBLE).addAlias("x")).addArg(SCommandArg().addAllowType(SCommandArgType.DOUBLE).addAlias("y")).addArg(SCommandArg().addAllowType(SCommandArgType.DOUBLE).addAlias("z")).setExecutor(
            Consumer<SCommandOnlyPlayerData> {
                it.sender.velocity = Vector(it.args[1].toDouble(),it.args[2].toDouble(),it.args[3].toDouble())
            }
        ))
    }
}