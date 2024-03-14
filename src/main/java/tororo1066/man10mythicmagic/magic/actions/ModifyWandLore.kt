package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.slikey.effectlib.math.EquationStore
import com.elmakers.mine.bukkit.utility.CompatibilityLib
import com.elmakers.mine.bukkit.utility.ConfigurationUtils
import com.elmakers.mine.bukkit.utility.TextUtils
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.NumberConversions
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min


class ModifyWandLore: CompoundAction() {

    private var digits = 0

    private class ModifyLoreLine(pattern: String?, var value: Any?) {
        var pattern: Pattern = Pattern.compile(CompatibilityLib.getCompatibilityUtils().translateColors(pattern))
        var numeric: Boolean = false
        var min: Double? = null
        var max: Double? = null
        var defaultValue: String? = null

        constructor(configuration: ConfigurationSection) : this(
            configuration.getString("pattern"),
            configuration["value"]
        ) {
            defaultValue = configuration.getString("default")
            if (defaultValue != null) defaultValue =
                CompatibilityLib.getCompatibilityUtils().translateColors(defaultValue)
            if (configuration.contains("min")) min = configuration.getDouble("min")
            if (configuration.contains("max")) max = configuration.getDouble("max")
            numeric = configuration.getBoolean("numeric", true)
        }

        fun match(line: String): String? {
            val matcher: Matcher = pattern.matcher(line)
            if (matcher.matches()) {
                return matcher.group(1)
            }
            return null
        }

        fun replace(line: String, value: String): String {
            val matcher: Matcher = pattern.matcher(line)
            matcher.find()
            return StringBuilder(line).replace(matcher.start(1), matcher.end(1), value).toString()
        }
    }

    private var modify: MutableList<ModifyLoreLine>? = null

    override fun prepare(context: CastContext?, parameters: ConfigurationSection) {
        digits = parameters.getInt("digits")
        modify = ArrayList()
        val modifyObject = parameters["modify"]
        if (modifyObject is ConfigurationSection) {
            val keys = modifyObject.getKeys(true)
            for (key in keys) {
                val property = ModifyLoreLine(key, modifyObject[key])
                modify?.add(property)
            }
        } else {
            val complex: Collection<ConfigurationSection> = ConfigurationUtils.getNodeList(parameters, "modify")
            for (section in complex) {
                val property = ModifyLoreLine(section)
                modify?.add(property)
            }
        }
    }

    override fun perform(context: CastContext): SpellResult {
        if (modify == null) {
            return SpellResult.FAIL
        }
        val wand = context.wand?:return SpellResult.NO_TARGET
        val lore = wand.getStringList("lore")?:return SpellResult.NO_TARGET

        var modified = 0
        for (property in modify!!) {
            val found = false
            for (i in lore.indices) {
                val line = lore[i]
                val originalValue = property.match(line)
                if (originalValue != null) {
                    if (property.value == null) continue
                    var value = ""
                    if (property.value is String) {
                        if (property.numeric) {
                            val transform = EquationStore.getInstance().getTransform(property.value as String?)
                            val currentValue = NumberConversions.toDouble(originalValue)
                            if (transform.isValid) {
                                transform.setVariable("x", currentValue)
                                var transformedValue = transform.get()
                                if (transformedValue.isNaN()) continue

                                if (property.max != null) {
                                    if (currentValue >= property.max!! && transformedValue >= property.max!!) continue
                                    transformedValue = min(transformedValue, property.max!!)
                                }
                                if (property.min != null) {
                                    if (currentValue <= property.min!! && transformedValue <= property.min!!) continue
                                    transformedValue = max(transformedValue, property.min!!)
                                }
                                value = TextUtils.printNumber(transformedValue, digits)
                            }
                        } else {
                            value = CompatibilityLib.getCompatibilityUtils().translateColors(property.value as String?)
                        }
                    } else if (property.value is Number) {
                        value = TextUtils.printNumber(NumberConversions.toDouble(originalValue), digits)
                    }

                    modified++
                    lore[i] = property.replace(line, value)
                    break
                }
            }
            if (!found && property.defaultValue != null) {
                lore.add(property.defaultValue!!)
                modified++
                continue
            }
        }

        wand.configure("lore", lore)

        if (modified == 0) return SpellResult.NO_TARGET


        return SpellResult.CAST
    }

    override fun getParameterNames(spell: Spell?, parameters: MutableCollection<String?>) {
        super.getParameterNames(spell, parameters)
        parameters.add("modify")
        parameters.add("digits")
    }

    override fun isUndoable(): Boolean {
        return false
    }

    override fun requiresTargetEntity(): Boolean {
        return true
    }

    override fun requiresTarget(): Boolean {
        return true
    }
}