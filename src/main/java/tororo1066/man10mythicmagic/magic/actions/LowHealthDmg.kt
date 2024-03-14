package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity

class LowHealthDmg: CompoundAction() {

    var multiply = 1.0

    override fun perform(context: CastContext): SpellResult {
        val entity = context.targetEntity?:return SpellResult.NO_TARGET
        if (entity !is LivingEntity)return SpellResult.NO_TARGET
        val damage = (context.mage.maxHealth - context.mage.health) * multiply
        entity.damage(damage,context.mage.entity)
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        multiply = parameters.getDouble("multiply",1.0)
    }
}