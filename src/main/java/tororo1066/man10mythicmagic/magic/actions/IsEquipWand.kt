package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import tororo1066.man10mythicmagic.Man10MythicMagic

class IsEquipWand: CheckAction() {

    lateinit var slot: EquipmentSlot
    var wandName = ""
    override fun isAllowed(context: CastContext): Boolean {
        val p = context.targetEntity?:return false
        if (p !is Player)return false
        return (Man10MythicMagic.magicAPI.controller.getIfWand(p.inventory.getItem(slot))?:return false).templateKey == wandName
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        slot = EquipmentSlot.valueOf(parameters.getString("slot","HAND")!!.uppercase())
        wandName = parameters.getString("wandName","")!!

    }
}