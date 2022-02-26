package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext

class CheckEnchantmentEffect : CheckAction() {
    override fun isAllowed(p0: CastContext?): Boolean {
        return ((p0?:return false).wand?:return false).enchantments.isNotEmpty()
    }

}