package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.magic.VariableScope
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.configuration.SpellParameters
import org.bukkit.configuration.ConfigurationSection

class UniqueVariable: CompoundAction() {

    var variable = ""
    var restoreName = ""
    var type = Type.STORE
    var scope = VariableScope.CAST

    lateinit var parameters: ConfigurationSection


    enum class Type {
        STORE,
        RESTORE,
        CLEAR
    }

    override fun perform(context: CastContext): SpellResult {
        val uuid = (context.targetEntity ?: return SpellResult.NO_TARGET).uniqueId
        when (type) {
            Type.STORE -> {
                val spellParameters = parameters as? SpellParameters
                if (spellParameters != null) {
                    if (spellParameters.context == null) {
                        spellParameters.context = context
                    }
                }
                val value = spellParameters?.getDouble("value") ?: parameters.getDouble("value")
                context.mage.variables.set(
                    "UniqueVariable-${uuid}-${variable}",
                    value
                )
            }

            Type.RESTORE -> {
                context.mage.variables.get("UniqueVariable-${uuid}-${variable}")?.let {
                    context.getVariables(scope).set(restoreName, it)
                }
            }

            Type.CLEAR -> {
                context.mage.variables.set("UniqueVariable-${uuid}-${variable}", null)
            }
        }

        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        variable = parameters.getString("variable", "") ?: "undefined"
        type = Type.valueOf(parameters.getString("type", "STORE")!!.uppercase())
        restoreName = parameters.getString("restore_name", "") ?: "undefined"
        scope = VariableScope.valueOf(parameters.getString("scope", "CAST")!!.uppercase())

        this.parameters = parameters
    }
}