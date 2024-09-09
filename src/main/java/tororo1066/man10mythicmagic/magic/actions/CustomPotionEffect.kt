package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import tororo1066.man10mythicmagic.magic.effect.CustomPotionManager

class CustomPotionEffect: CompoundAction() {

    val removeEffects = ArrayList<String>()
    val addEffects = ArrayList<Pair<String, Int>>()
    var duration = 0
    var private = false

    override fun perform(context: CastContext): SpellResult {
        val mage = context.mage
        val target = context.targetEntity ?: return SpellResult.NO_TARGET
        removeEffects.forEach { effect ->
            CustomPotionManager.removePotionEffect(
                target, effect, if (private) mage.entity.uniqueId else null
            )
        }

        addEffects.forEach { (effect, amplifier) ->
            CustomPotionManager.addPotionEffect(
                target, effect, duration / 50, amplifier, if (private) mage.entity.uniqueId else null
            )
        }

        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        addEffects.clear()
        removeEffects.clear()
        super.prepare(context, parameters)
        duration = parameters.getInt("duration", 1000)
        val addEffects = parameters.getConfigurationSection("add_effects")
        val removeEffects = parameters.getStringList("remove_effects")
        if (addEffects != null) {
            addEffects.getKeys(false).forEach { key ->
                this.addEffects.add(Pair(key, addEffects.getInt(key)))
            }
        }
        this.removeEffects.addAll(removeEffects)
        private = parameters.getBoolean("private", false)
    }
}