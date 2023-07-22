package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import io.lumine.mythic.bukkit.BukkitAPIHelper
import org.bukkit.configuration.ConfigurationSection

class CallMythicSkill : CompoundAction() {

    private var skill = ""

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        skill = parameters.getString("skill","")!!
    }

    override fun start(context: CastContext): SpellResult {
        return if (BukkitAPIHelper().castSkill(context.entity,skill)){
            SpellResult.CAST
        } else {
            SpellResult.FAIL
        }
    }
}