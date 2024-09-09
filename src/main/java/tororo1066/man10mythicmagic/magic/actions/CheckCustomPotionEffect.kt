package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext
import org.bukkit.configuration.ConfigurationSection
import tororo1066.man10mythicmagic.magic.effect.CustomPotionManager

class CheckCustomPotionEffect: CheckAction() {

    val effects = HashMap<String, Pair<Int, Int>>() // effect name, min level, max level
    var private = false

    override fun isAllowed(context: CastContext): Boolean {
        val entity = context.targetEntity ?: return false
        CustomPotionManager.customPotionEffectInstances[entity.uniqueId]?.forEach { (_, effects) ->
            effects.forEach second@ {
                if (this.effects.containsKey(it.effect.key)) {
                    val effect = this.effects[it.effect.key]!!

                    if ((private && it.player != context.mage.entity.uniqueId) || (!private && it.player != null)) {
                        return@second
                    }
                    if (it.amplifier in effect.first..effect.second) {
                        return true
                    }
                }
            }
        }

        return false
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        effects.clear()
        val effects = parameters.getConfigurationSection("effects")
        effects?.getKeys(false)?.forEach { key ->
            if (effects.isInt(key)) {
                val value = effects.getInt(key)
                this.effects[key] = Pair(value, value)
            } else if (effects.isConfigurationSection(key)) {
                val effect = effects.getConfigurationSection(key)!!
                val min: Int
                val max: Int
                if (effect.isInt("value")) {
                    val value = effect.getInt("value")
                    min = value
                    max = value
                } else {
                    min = effect.getInt("min", Int.MIN_VALUE)
                    max = effect.getInt("max", Int.MAX_VALUE)
                }
                this.effects[key] = Pair(min, max)
            }
        }
        private = parameters.getBoolean("private", false)
    }
}