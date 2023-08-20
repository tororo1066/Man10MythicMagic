package tororo1066.man10mythicmagic.noused

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection

class MovingPush : CompoundAction() {

    var length = 1.0

    override fun start(context: CastContext): SpellResult {
        val entity = context.targetEntity ?: return SpellResult.NO_TARGET
        val vector = MovementListener.playersVelocity[entity.uniqueId]?:return SpellResult.FAIL
        entity.velocity = vector.setY(0).multiply(length)
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        length = parameters.getDouble("length", 1.0)
    }
}