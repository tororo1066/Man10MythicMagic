package tororo1066.man10mythicmagic.utils

import com.elmakers.mine.bukkit.api.magic.Mage
import com.elmakers.mine.bukkit.api.spell.Spell
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

fun getAllWands(p: Player): List<Wand> {
    val wands = ArrayList<Wand>()

    p.inventory.contents.forEach {
        val wand = Man10MythicMagic.magicAPI.controller.getIfWand(it)?:return@forEach
        wands.add(wand)
    }

    return wands
}

fun getHotbarWands(p: Player): List<Wand> {
    val wands = ArrayList<Wand>()

//    p.inventory.contents.forEachIndexed { index, itemStack ->
//        if (index < 9){
//            val wand = Man10MythicMagic.magicAPI.controller.getIfWand(itemStack)?:return@forEachIndexed
//            wands.add(wand)
//        }
//    }
    for (i in 0..8) {
        val wand = Man10MythicMagic.magicAPI.controller.getIfWand(p.inventory.getItem(i)) ?: continue
        wands.add(wand)
    }

    return wands
}

fun getAllWandSpells(wand: Wand, mage: Mage): List<Spell> {
    if (wand !is com.elmakers.mine.bukkit.wand.Wand) return emptyList()
    val spells = ArrayList<Spell>()
    for (i in 0..6) {
        spells.add(wand.getAlternateSpell(i) ?: continue)
    }

    wand.activeSpell?.let { spells.add(it) }

    wand.spells.forEach { spells.add(wand.getSpell(it, mage) ?: return@forEach) }

    return spells
}