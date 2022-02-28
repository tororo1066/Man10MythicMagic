package tororo1066.man10mythicmagic.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Material
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandObject
import tororo1066.tororopluginapi.sCommand.SCommandOnlyPlayerData
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer

class MMMCommands : SCommand("mythicmagic","","mmm.op") {

    init {
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("itemInfo"))
                .setExecutor(Consumer<SCommandOnlyPlayerData> {
                    if (it.sender.inventory.itemInMainHand.type.isAir){
                        it.sender.sendMessage("§4手にアイテムを持ってください")
                        return@Consumer
                    }

                    it.sender.sendMessage(Component.text("§a§lここをクリックでコピー").hoverEvent(HoverEvent.showText(Component.text("§b§lここをクリック！"))).clickEvent(ClickEvent.copyToClipboard(SItem(it.sender.inventory.itemInMainHand).toBase64())))
                }))
    }
}