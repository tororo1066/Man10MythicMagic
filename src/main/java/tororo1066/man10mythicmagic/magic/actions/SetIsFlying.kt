package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class SetIsFlying: CompoundAction() {

    var fly = true

    override fun start(context: CastContext): SpellResult {
        val entity = context.targetEntity
        if (entity !is Player)return SpellResult.FAIL
        entity.allowFlight = fly
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        fly = parameters.getBoolean("fly",true)
    }
}