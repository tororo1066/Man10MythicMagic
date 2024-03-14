package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

class CheckCMD : CheckAction() {

    private var cmd = 0
    lateinit var slot : EquipmentSlot

    override fun isAllowed(p0: CastContext): Boolean {
        val target = p0.targetEntity?:return false
        if (target !is Player)return false
        val item = target.inventory.getItem(slot)
        return item.itemMeta.customModelData == cmd
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        cmd = parameters.getInt("cmd")
        slot = EquipmentSlot.valueOf(parameters.getString("slot","hand")!!.uppercase())
    }
}