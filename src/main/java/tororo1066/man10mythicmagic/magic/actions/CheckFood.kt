package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class CheckFood: CheckAction() {
    var amount = 20

    override fun isAllowed(context: CastContext): Boolean {
        val p = context.targetEntity
        if (p !is Player)return false
        return p.foodLevel >= amount
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        amount = parameters.getInt("amount",20)
    }
}