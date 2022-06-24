package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.meta.Damageable

class CheckDurability: CheckAction() {

    var min = -1
    var max = -1

    override fun isAllowed(context: CastContext): Boolean {
        val meta = (context.wand?.item?:return false).itemMeta
        if (meta !is Damageable)return false
        when(min){
            -1 -> {
                if (max == -1)return false
                return max >= meta.damage
            }
            else -> {
                if (max == -1){
                    return min <= meta.damage
                }
                return meta.damage in min..max
            }
        }
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        min = parameters.getInt("min",-1)
        max = parameters.getInt("max",-1)
    }
}