package tororo1066.man10mythicmagic.magic.effect

import com.elmakers.mine.bukkit.action.ActionHandler
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.batch.Batch
import com.elmakers.mine.bukkit.api.batch.SpellBatch
import com.elmakers.mine.bukkit.api.block.MaterialAndData
import com.elmakers.mine.bukkit.api.magic.VariableScope
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.spell.BrushSpell
import com.elmakers.mine.bukkit.utility.ConfigurationUtils
import com.elmakers.mine.bukkit.utility.Target
import com.elmakers.mine.bukkit.utility.Targeting
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import java.util.Locale
import java.util.UUID
import java.util.logging.Level

class CustomPotionEffect: BrushSpell() {

    var tickInterval: Int = 1
    var playerSpecific: Boolean = false
    var castRemoveOnOverride: Boolean = false
    var removeOnDeath: Boolean = false

    private val actions: MutableMap<String, ActionHandler> = HashMap()
    private var undoable = false
    private var requiresBuildPermission = false
    private var requiresBreakPermission = false
    private var currentHandler: ActionHandler? = null
    private val handlerParameters: MutableMap<String, ConfigurationSection> = HashMap()
    private var workThreshold = 500

    override fun processResult(result: SpellResult, castParameters: ConfigurationSection?) {
        if (result != SpellResult.CAST && !result.isAlternate) {
            actions[result.name.lowercase(Locale.getDefault())]?.cast(this.currentCast, castParameters)
        }

        super.processResult(result, castParameters)
    }

    override fun isLegacy(): Boolean {
        return false
    }

    override fun isBatched(): Boolean {
        return true
    }

    override fun hasHandlerParameters(handlerKey: String?): Boolean {
        return handlerParameters.containsKey(handlerKey)
    }

    override fun getHandlerParameters(handlerKey: String?): ConfigurationSection? {
        return handlerParameters[handlerKey]
    }

    override fun processParameters(parameters: ConfigurationSection?) {
        var processParameters = parameters
        val handlerKey = parameters?.getString("handler")
        val alternateParameters: ConfigurationSection? = getHandlerParameters(handlerKey)

        if (alternateParameters != null) {
            processParameters = if (processParameters == null) {
                alternateParameters
            } else {
                ConfigurationUtils.addConfigurations(processParameters, alternateParameters, true)
            }
        }

        super.processParameters(processParameters)
    }

    override fun onCast(parameters: ConfigurationSection): SpellResult {
        currentCast.workAllowed = this.workThreshold
        this.currentHandler = actions[parameters.getString("handler")]

        return this.startCast(SpellResult.CAST, parameters)
    }

    override fun findTarget(context: CastContext?): Target {
        if (!playerSpecific) return super.findTarget(context)
        val grantor = workingParameters?.getString("grantor")
        if (grantor != null) {
            val player = Bukkit.getPlayer(UUID.fromString(grantor))
            if (player != null) {
                val targeting = javaClass.getField("targeting")
                targeting.isAccessible = true
                val target = targeting.get(this) as Targeting
                return target.overrideTarget(context, Target(eyeLocation, player))
            }
        }

        return super.findTarget(context)
    }

    protected fun startCast(spellResult: SpellResult, parameters: ConfigurationSection?): SpellResult {
        var result = spellResult
        if (this.isUndoable) {
            getMage().prepareForUndo(this.undoList)
        }

        this.target()
        if (this.currentHandler != null) {
            this.currentHandler = currentHandler!!.clone() as ActionHandler
            currentCast.rootHandler = this.currentHandler
            currentCast.alternateResult = result
            currentCast.getVariables(VariableScope.CAST).run {
                set("duration", parameters?.getInt("duration") ?: 0)
                set("amplifier", parameters?.getInt("amplifier") ?: 0)
            }

            try {
                currentCast.initialResult = result
                result = currentHandler!!.cast(this.currentCast, parameters)
                result = currentCast.initialResult.max(result)
                currentCast.initialResult = result
            } catch (var6: Exception) {
                controller.logger.log(Level.WARNING, "Spell cast failed for " + this.key, var6)
                result = SpellResult.FAIL

                try {
                    currentCast.result = result
                    currentCast.initialResult = result
                    currentHandler!!.finish(this.currentCast)
                    currentCast.finish()
                } catch (var5: Exception) {
                    controller.logger.log(Level.WARNING, "Failed to clean up failed spell " + this.key, var5)
                }
            }
        } else {
            currentCast.result = result
            currentCast.initialResult = result
            currentCast.finish()
        }

        return result
    }

    override fun reloadParameters(context: CastContext) {
        val handler = context.rootHandler
        handler?.prepare(context, context.workingParameters)
    }

    override fun loadTemplate(template: ConfigurationSection) {
        this.castOnNoTarget = true
        super.loadTemplate(template)
        this.undoable = false
        this.requiresBuildPermission = false
        this.requiresBreakPermission = false
        this.usesBrush = template.getBoolean("uses_brush", false)
        this.tickInterval = template.getInt("tick_interval", 1)
        this.playerSpecific = template.getBoolean("player_specific", false)
        this.castRemoveOnOverride = template.getBoolean("cast_remove_on_override", false)
        this.removeOnDeath = template.getBoolean("remove_on_death", true)
        var actionsNode = template.getConfigurationSection("actions")
        if (actionsNode != null) {
            val parameters = template.getConfigurationSection("parameters")
            val templateKeys = template.getKeys(false).iterator()

            while (templateKeys.hasNext()) {
                val templateKey = templateKeys.next()
                if (templateKey.endsWith("_parameters")) {
                    val overrides = ConfigurationUtils.cloneConfiguration(template.getConfigurationSection(templateKey))
                    val configKey = templateKey.substring(0, templateKey.length - 11)
                    handlerParameters[configKey] = overrides
                }
            }

            this.workingParameters = parameters
            actionsNode = ConfigurationUtils.replaceParameters(actionsNode, parameters)
            val actionKeys = actionsNode!!.getKeys(false).iterator()

            while (actionKeys.hasNext()) {
                val actionKey = actionKeys.next()
                var configKey = actionKey
                if (actionsNode.isString(actionKey)) {
                    configKey = actionsNode.getString(actionKey)!!
                }

                val handler = ActionHandler()
                handler.load(this, actionsNode, configKey)
                handler.initialize(this, parameters)
                this.usesBrush = this.usesBrush || handler.usesBrush()
                this.undoable = this.undoable || handler.isUndoable
                this.requiresBuildPermission = this.requiresBuildPermission || handler.requiresBuildPermission()
                this.requiresBreakPermission = this.requiresBreakPermission || handler.requiresBreakPermission()
                actions[actionKey] = handler
            }
        } else if (template.contains("actions")) {
            controller.logger.warning("Invalid actions configuration in spell " + this.key + ", did you forget to add cast: ?")
        }

        this.undoable = template.getBoolean("undoable", this.undoable)
        this.requiresBreakPermission = template.getBoolean("require_break", this.requiresBreakPermission)
        this.requiresBuildPermission = template.getBoolean("require_build", this.requiresBuildPermission)
    }

    override fun isUndoable(): Boolean {
        return this.undoable
    }

    override fun getParameters(parameters: MutableCollection<String?>) {
        super.getParameters(parameters)
        val var2: Iterator<*> = actions.values.iterator()

        while (var2.hasNext()) {
            val handler = var2.next() as ActionHandler
            handler.getParameterNames(this, parameters)
        }

        parameters.add("require_break")
        parameters.add("require_build")
    }

    override fun getParameterOptions(examples: MutableCollection<String?>, parameterKey: String) {
        super.getParameterOptions(examples, parameterKey)
        val var3 = actions.values.iterator()

        while (var3.hasNext()) {
            val handler = var3.next()
            handler.getParameterOptions(this, parameterKey, examples)
        }

        if (parameterKey == "require_break" || parameterKey == "require_build") {
            examples.addAll(listOf(*EXAMPLE_BOOLEANS))
        }
    }

    override fun getMessage(messageKey: String?, def: String?): String {
        var message = super.getMessage(messageKey, def)
        if (this.currentHandler != null) {
            message = currentHandler!!.transformMessage(message)
        }

        return message
    }

    override fun requiresBuildPermission(): Boolean {
        return this.requiresBuildPermission && !this.brushIsErase()
    }

    override fun requiresBreakPermission(): Boolean {
        return this.requiresBreakPermission || this.requiresBuildPermission && this.brushIsErase()
    }

    override fun getEffectMaterial(): MaterialAndData? {
        return if (!this.usesBrush) null else super.getEffectMaterial()
    }

    override fun isActive(): Boolean {
        if (this.mage == null) {
            return false
        } else if (this.toggle == ToggleType.UNDO && (this.toggleUndo != null) && !toggleUndo.isUndone) {
            return true
        } else {
            val pendingBatches = mage.pendingBatches
            val var2 = pendingBatches.iterator()

            var batch: Batch?
            do {
                if (!var2.hasNext()) {
                    return false
                }

                batch = var2.next()
            } while (batch !is SpellBatch || batch.spell !== this)

            return true
        }
    }

    override fun onReactivate(): Boolean {
        if (this.isActive) {
            return false
        } else {
            val prepared = this.prepareCast()
            if (prepared != null && !prepared) {
                return false
            } else {
                if (this.toggle != ToggleType.NONE) {
                    this.isActive = true
                }

                this.currentHandler = actions["reactivate"]
                if (this.currentHandler == null) {
                    this.currentHandler = actions["cast"]
                }

                return startCast(SpellResult.REACTIVATE, this.getCurrentCast().workingParameters).isSuccess
            }
        }
    }

    fun getHandlers(): Collection<String?> {
        return actions.keys
    }

    fun setCurrentHandler(handlerKey: String?, context: com.elmakers.mine.bukkit.action.CastContext) {
        this.currentHandler = actions[handlerKey]
        context.rootHandler = currentHandler
    }
}