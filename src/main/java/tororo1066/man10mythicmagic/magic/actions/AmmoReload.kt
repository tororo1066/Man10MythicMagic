package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10mythicmagic.Man10MythicMagic

class AmmoReload : CheckAction() {

    private var ammoName = ""
    private lateinit var material : Material
    private var customModelData = 0
    private var amount = 1

    override fun addHandlers(spell: Spell?, parameters: ConfigurationSection?) {
        addHandler(spell,"fail")
        addHandler(spell,"actions")
    }

    override fun isAllowed(context: CastContext?): Boolean {
        context?:return false
        if (context.entity !is Player)return false
        val p = context.entity as Player
        val allAmmo = ArrayList<ItemStack>()
        for (content in p.inventory.contents){
            content?:continue
            if (content.type.isAir)continue
            if (content.type != material)continue
            if (ammoName != "" && ammoName != content.itemMeta.displayName)continue
            if (customModelData == 0 && content.itemMeta.hasCustomModelData())continue
            if (customModelData != 0 && customModelData != content.itemMeta.customModelData)continue
            allAmmo.add(content)
        }
        if (allAmmo.isEmpty()){
            return false
        }
        var count = 0
        allAmmo.forEach {
            count += it.amount
        }


        if (count < amount){
            return false
        }

        var summingAmount = amount

        for (content in p.inventory.contents){
            content?:continue
            if (content.type.isAir)continue
            if (content.type != material)continue
            if (ammoName != "" && ammoName != content.itemMeta.displayName)continue
            if (customModelData == 0 && content.itemMeta.hasCustomModelData())continue
            if (customModelData != 0 && customModelData != content.itemMeta.customModelData)continue
            if (content.amount > summingAmount){
                content.amount -= summingAmount
                break
            } else {
                summingAmount -= content.amount
                content.amount = 0
            }
        }

        return true
    }

    override fun prepare(context: CastContext?, parameters: ConfigurationSection?) {
        context?:return
        parameters?:return

        material = Material.getMaterial(parameters.getString("material")?:"STONE")?:Material.STONE
        customModelData = parameters.getInt("cmd",0)
        amount = parameters.getInt("amount",1)
        ammoName = parameters.getString("name","")!!
    }
}