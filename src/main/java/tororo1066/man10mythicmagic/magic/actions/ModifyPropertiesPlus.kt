package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.magic.CasterProperties
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.configuration.SpellParameters
import com.elmakers.mine.bukkit.spell.BaseSpell
import com.elmakers.mine.bukkit.utility.ConfigurationUtils
import com.elmakers.mine.bukkit.utility.SpellUtils
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.NumberConversions
import kotlin.math.max
import kotlin.math.min

class ModifyPropertiesPlus: CompoundAction() {

    private class ModifyProperty {
        var path: String?
        var value: Any?
        var min: Double? = null
        var max: Double? = null
        var defaultValue: Double? = null

        constructor(path: String?, value: Any?) {
            this.path = path
            this.value = value
        }

        constructor(configuration: ConfigurationSection) {
            path = configuration.getString("property", configuration.getString("path"))
            value = configuration["value"]
            if (configuration.contains("min")) min = configuration.getDouble("min")
            if (configuration.contains("max")) max = configuration.getDouble("max")
            if (configuration.contains("default")) defaultValue = configuration.getDouble("default")
        }
    }

    private lateinit var modify: MutableList<ModifyProperty>
    private var modifyTarget: String? = null
    private var originalVariable: String? = null
    private var extraParameters: SpellParameters? = null
    private var upgrade = false
    private var bypassUndo = false

    private class ModifyPropertyUndoAction(
        private val original: ConfigurationSection,
        private val properties: CasterProperties
    ) :
        Runnable {
        override fun run() {
            properties.configure(original)
        }
    }

    override fun prepare(context: CastContext?, parameters: ConfigurationSection) {
        modifyTarget = parameters.getString("modify_target", "wand")
        upgrade = parameters.getBoolean("upgrade", false)
        bypassUndo = parameters.getBoolean("bypass_undo", false)
        originalVariable = parameters.getString("original_variable", "x")

        modify = ArrayList()
        val modifyObject = parameters["modify"]
        if (modifyObject is ConfigurationSection) {
            val keys = modifyObject.getKeys(true)
            for (key in keys) {
                val property = ModifyProperty(key, modifyObject[key!!])
                modify.add(property)
            }
        } else {
            val complex: Collection<ConfigurationSection> = ConfigurationUtils.getNodeList(parameters, "modify")
            for (section in complex) {
                val property = ModifyProperty(section)
                modify.add(property)
            }
        }
        if (parameters is SpellParameters) {
            extraParameters = parameters
        }
    }

    override fun perform(context: CastContext): SpellResult {
        if (extraParameters == null || !::modify.isInitialized) {
            return SpellResult.FAIL
        }
        val properties = context.getTargetCasterProperties(modifyTarget)
            ?: return SpellResult.NO_TARGET
        val original = ConfigurationUtils.newConfigurationSection()
        val changed = ConfigurationUtils.newConfigurationSection()
        for (property in modify) {
            val originalValue = properties.getProperty(property.path!!)
            var newValue = property.value
            if ((originalValue == null || originalValue is Number) && property.value is String) {
                // Allow using attributes and variables here
                val defaultValue = if (property.defaultValue == null) 0.0 else property.defaultValue!!
                val originalDouble =
                    if (originalValue == null) defaultValue else NumberConversions.toDouble(originalValue)

                val valueString = context.parameterize(property.value as String?)

                var transformedValue = SpellUtils.modifyProperty(
                    originalDouble,
                    valueString,
                    originalVariable,
                    extraParameters
                )
                if (transformedValue != null) {
                    if (property.max != null) {
                        if (originalDouble >= property.max!! && transformedValue >= property.max!!) continue
                        transformedValue = min(transformedValue, property.max!!)
                    }
                    if (property.min != null) {
                        if (originalDouble <= property.min!! && transformedValue <= property.min!!) continue
                        transformedValue = max(transformedValue, property.min!!)
                    }
                    newValue = transformedValue
                }
            } else if (property.value is String) {
                newValue = context.parameterize(property.value as String?)
            }

            changed[property.path!!] = newValue
            original[property.path!!] = originalValue
        }

        if (changed.getKeys(false).isEmpty()) return SpellResult.NO_TARGET
        if (upgrade) properties.upgrade(changed)
        else properties.configure(changed)
        if (!bypassUndo) {
            context.registerForUndo(ModifyPropertyUndoAction(original, properties))
        }
        return SpellResult.CAST
    }

    override fun getParameterNames(spell: Spell?, parameters: MutableCollection<String?>) {
        super.getParameterNames(spell, parameters)
        parameters.add("modify")
        parameters.add("modify_target")
        parameters.add("original_variable")
        parameters.add("upgrade")
    }

    override fun getParameterOptions(spell: Spell, parameterKey: String, examples: MutableCollection<String?>) {
        if (parameterKey == "upgrade") {
            examples.addAll(listOf(*BaseSpell.EXAMPLE_BOOLEANS))
        } else if (parameterKey == "modify_target") {
            examples.add("player")
            examples.add("wand")
            examples.addAll(spell.controller.mageClassKeys)
        } else {
            super.getParameterOptions(spell, parameterKey, examples)
        }
    }

    override fun isUndoable(): Boolean {
        return true
    }
}