package tororo1066.man10mythicmagic.utils

import com.elmakers.mine.bukkit.api.wand.Wand
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.Man10MythicMagic

fun getAllUsingWands(p: Player): List<Wand> {
    val mage = Man10MythicMagic.magicAPI.controller.getRegisteredMage(p)?:return emptyList()
    val wands = ArrayList<Wand>()

    if (mage.activeWand != null){
        wands.add(mage.activeWand)
    }

    p.inventory.armorContents.forEach {
        val wand = Man10MythicMagic.magicAPI.controller.getIfWand(it)?:return@forEach
        wands.add(wand)
    }

    return wands
}