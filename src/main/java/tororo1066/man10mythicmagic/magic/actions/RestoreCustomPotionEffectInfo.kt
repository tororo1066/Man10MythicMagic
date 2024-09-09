package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.magic.VariableScope
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import tororo1066.man10mythicmagic.magic.effect.CustomPotionManager

class RestoreCustomPotionEffectInfo: CompoundAction() {

    var effect = ""
    var scope = VariableScope.CAST
    var private = false

    private fun default(context: CastContext) {
        context.getVariables(scope).set("${effect}_amplifier", 0)
        context.getVariables(scope).set("${effect}_duration", 0)
    }

    override fun perform(context: CastContext): SpellResult {
        val entity = context.targetEntity ?: return SpellResult.NO_TARGET
        val effects = CustomPotionManager.customPotionEffectInstances[entity.uniqueId]?.get(effect)

        if (effects == null) {
            default(context)
            context.spell.reloadParameters(context)
            return SpellResult.CAST
        }

        val value = if (private) {
            effects.filter { it.player == context.mage.entity.uniqueId }.maxByOrNull { it.amplifier }
        } else {
            effects.maxByOrNull { it.amplifier }
        }

        if (value != null) {
            context.getVariables(scope).set("${effect}_amplifier", value.amplifier)
            context.getVariables(scope).set("${effect}_duration", value.duration - value.currentTick)
        } else {
            default(context)
        }


        context.spell.reloadParameters(context)
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        effect = parameters.getString("effect", "") ?: "undefined"
        scope = VariableScope.valueOf(parameters.getString("scope", "CAST")!!.uppercase())
        private = parameters.getBoolean("private", false)
    }
}