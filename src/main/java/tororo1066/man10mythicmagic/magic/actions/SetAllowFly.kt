package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class SetAllowFly: CompoundAction() {

    var allow = true

    override fun start(context: CastContext): SpellResult {
        val entity = context.targetEntity
        if (entity !is Player)return SpellResult.FAIL
        entity.allowFlight = allow
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        allow = parameters.getBoolean("allow",true)
    }
}