package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10mythicmagic.Man10MythicMagic

class AmmoReload : CompoundAction() {

    var ammoName = ""
    lateinit var material : Material
    var customModelData = 0
    var amount = 1
    var useAmmoPlugin = false

    override fun addHandlers(spell: Spell?, parameters: ConfigurationSection?) {
        addHandler(spell,"fail")
        addHandler(spell,"actions")
    }


    override fun perform(context: CastContext?): SpellResult {
        context?:return SpellResult.FAIL
        if (context.entity !is Player)return SpellResult.FAIL
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
            if (useAmmoPlugin){
                if (Man10MythicMagic.ammoAPI.removeAmmo(p,material,if (ammoName == "") "_" else ammoName,customModelData,amount)){
                    getHandler("actions")?.cast(context,context.workingParameters)
                    return SpellResult.CAST
                }
            }
            getHandler("fail")?.cast(context,context.workingParameters)
            return SpellResult.CAST
        }
        var count = 0
        allAmmo.forEach {
            count += it.amount
        }


        if (count < amount){
            if (useAmmoPlugin){
                if (Man10MythicMagic.ammoAPI.removeAmmo(p,material,if (ammoName == "") "_" else ammoName,customModelData,amount)){
                    getHandler("actions")?.cast(context,context.workingParameters)
                    return SpellResult.CAST
                }
            }
            getHandler("fail")?.cast(context,context.workingParameters)
            return SpellResult.CAST
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


        getHandler("actions")?.cast(context,context.workingParameters)
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext?, parameters: ConfigurationSection?) {
        context?:return
        parameters?:return

        material = Material.getMaterial(parameters.getString("material")?:"STONE")?:Material.STONE
        customModelData = parameters.getInt("cmd",0)
        amount = parameters.getInt("amount",1)
        ammoName = parameters.getString("name","")!!
        useAmmoPlugin = parameters.getBoolean("ammoPlugin",false)
    }
}