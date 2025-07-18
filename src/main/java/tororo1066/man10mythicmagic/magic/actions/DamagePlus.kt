package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.BaseSpellAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.magic.SourceLocation
import com.elmakers.mine.bukkit.spell.BaseSpell
import com.elmakers.mine.bukkit.utility.CompatibilityLib
import org.bukkit.ChatColor
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Damageable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import tororo1066.man10mythicmagic.Man10MythicMagic
import kotlin.math.max
import kotlin.math.min

@Suppress("DEPRECATION")
class DamagePlus: BaseSpellAction() {

    private var entityDamage: Double = 0.0
    private var playerDamage: Double = 0.0
    private var magicDamage = false
    private var magicEntityDamage = false
    private var invertDistance = false
    private var setLastDamaged = false
    private var setLastDamagedBy = false
    private var percentage: Double? = null
    private var knockbackResistance: Double? = null
    private var damageMultiplier: Double? = null
    private var maxDistanceSquared = 0.0
    private var minDistanceSquared = 0.0
    private var minDamage = 0.0
    private var damageType: String? = null
    private var damageSourceLocation: SourceLocation? = null
    private var criticalProbability = 0.0
    private var criticalMultiplier = 0.0
    private var noDamageTicks = 0
    private var cancelOnKillTarget = false
    private var criticalEffectsKey: String? = null

    private var element: String? = null
    private var killMessage: List<String>? = null

    override fun prepare(context: CastContext?, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        this.noDamageTicks = parameters.getInt("no_damage_ticks", -1)
        val damage = parameters.getDouble("damage", 1.0)
        this.entityDamage = parameters.getDouble("entity_damage", damage)
        this.playerDamage = parameters.getDouble("player_damage", damage)
        this.invertDistance = parameters.getBoolean("invert_distance", false)
        this.setLastDamaged = parameters.getBoolean("set_last_damaged", true)
        this.setLastDamagedBy = parameters.getBoolean("set_last_damaged_by", true)
        this.cancelOnKillTarget = parameters.getBoolean("cancel_on_kill_target", false)
        if (parameters.contains("damage_multiplier")) {
            this.damageMultiplier = parameters.getDouble("damage_multiplier")
        } else {
            this.damageMultiplier = null
        }

        if (parameters.contains("percentage")) {
            this.percentage = parameters.getDouble("percentage")
        } else {
            this.percentage = null
        }

        this.magicDamage = parameters.getBoolean("magic_damage", true)
        this.magicEntityDamage = parameters.getBoolean("magic_entity_damage", this.magicDamage)
        if (parameters.contains("knockback_resistance")) {
            this.knockbackResistance = parameters.getDouble("knockback_resistance")
        } else {
            this.knockbackResistance = null
        }

        val maxDistance = parameters.getDouble("damage_max_distance")
        this.maxDistanceSquared = maxDistance * maxDistance
        val minDistance = parameters.getDouble("damage_min_distance", 0.0)
        this.minDistanceSquared = minDistance * minDistance
        this.minDamage = parameters.getDouble("min_damage", 0.0)
        this.damageType = parameters.getString("damage_type")
        this.damageSourceLocation = SourceLocation(parameters.getString("damage_source_location", "BODY"), false)
        this.criticalProbability = parameters.getDouble("critical_probability", 0.0)
        this.criticalMultiplier = parameters.getDouble("critical_damage_multiplier", 0.0)
        this.criticalEffectsKey = parameters.getString("critical_effects", "critical")

        this.element = parameters.getString("element")
        if (parameters.isList("kill_message")) {
            this.killMessage = parameters.getStringList("kill_message")
        } else if (parameters.isString("kill_message")) {
            this.killMessage = listOf(parameters.getString("kill_message")!!)
        }
    }

    override fun perform(context: CastContext): SpellResult? {
        val entity = context.targetEntity
        if (entity != null && entity is Damageable && !entity.isDead) {
            var damage: Double
            val targetEntity = entity
            val livingTarget = entity as? LivingEntity
            context.registerDamaged(targetEntity)
            val mage = context.mage
            val controller = context.controller
            var previousKnockbackResistance = 0.0

            try {
                if (this.element != null) {
                    targetEntity.setMetadata("element", FixedMetadataValue(Man10MythicMagic.plugin, this.element))
                }

                if (this.killMessage != null) {
                    val killMessage = this.killMessage!!.randomOrNull()
                    if (killMessage != null) {
                        targetEntity.setMetadata(
                            "killMessage",
                            FixedMetadataValue(Man10MythicMagic.plugin, killMessage)
                        )
                    }
                }

                if (this.knockbackResistance != null && livingTarget != null) {
                    val knockBackAttribute = livingTarget.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)
                    previousKnockbackResistance = knockBackAttribute!!.baseValue
                    knockBackAttribute.baseValue = this.knockbackResistance!!
                }

                if (targetEntity is LivingEntity && this.noDamageTicks >= 0) {
                    val li = targetEntity
                    li.noDamageTicks = this.noDamageTicks
                }

                damage = if (this.percentage != null) {
                    this.percentage!! * CompatibilityLib.getCompatibilityUtils().getMaxHealth(targetEntity)
                } else if (targetEntity is Player) {
                    this.playerDamage
                } else {
                    this.entityDamage
                }

                var multiplierType = this.damageType
                if (multiplierType == null) {
                    multiplierType = if (this.magicDamage) "magic" else "physical"
                }

                var mageMultiplier = mage.getDamageMultiplier(multiplierType)
                damage *= mageMultiplier
                if (this.maxDistanceSquared > 0.0) {
                    val entityLocation = this.damageSourceLocation!!.getLocation(context)
                    var distanceSquared = context.location!!.distanceSquared(entityLocation!!)
                    if (distanceSquared > this.maxDistanceSquared) {
                        if (!this.invertDistance) {
                            val var34 = SpellResult.NO_TARGET
                            return var34
                        }

                        distanceSquared = this.maxDistanceSquared
                    }

                    val distanceRange = this.maxDistanceSquared - this.minDistanceSquared
                    var distanceScale =
                        min(1.0, max(0.0, distanceSquared - this.minDistanceSquared) / distanceRange)
                    if (!this.invertDistance) {
                        distanceScale = 1.0 - distanceScale
                    }

                    damage *= distanceScale
                }

                if (this.damageMultiplier != null) {
                    damage *= this.damageMultiplier!!
                    mageMultiplier *= this.damageMultiplier!!
                }

                if (this.criticalProbability > 0.0 && this.criticalMultiplier > 0.0 && context.getRandom()
                        .nextDouble() <= this.criticalProbability
                ) {
                    damage *= this.criticalMultiplier
                    context.playEffects(this.criticalEffectsKey)
                }

                damage = max(this.minDamage, damage)
                if (this.damageType != null) {
                    mage.lastDamageDealtType = this.damageType
                    val targetMage = controller.getRegisteredMage(targetEntity)
                    var targetAnnotation = ""
                    if (targetMage != null) {
                        targetMage.lastDamageType = this.damageType
                    } else {
                        targetAnnotation = "*"
                    }

                    mage.sendDebugMessage(
                        ChatColor.RED.toString() + "Damaging (" + ChatColor.DARK_RED + this.damageType + ChatColor.RED + ") x " + ChatColor.DARK_RED + mageMultiplier + ChatColor.RED + " to " + ChatColor.BLUE + targetEntity.type + targetAnnotation + ": " + ChatColor.RED + damage,
                        5
                    )
                    if (mage.isPlayer && controller.damageTypes.contains(this.damageType)) {
                        CompatibilityLib.getCompatibilityUtils().magicDamage(targetEntity, damage, mage.entity)
                    } else {
                        CompatibilityLib.getCompatibilityUtils()
                            .damage(targetEntity, damage, mage.entity, this.damageType)
                    }
                } else if (!this.magicDamage || !this.magicEntityDamage && targetEntity !is Player) {
                    mage.sendDebugMessage(
                        ChatColor.RED.toString() + "Damaging x " + ChatColor.DARK_RED + mageMultiplier + ChatColor.RED + " to " + ChatColor.BLUE + targetEntity.type + ": " + damage,
                        5
                    )
                    CompatibilityLib.getCompatibilityUtils().damage(targetEntity, damage, mage.entity)
                } else {
                    mage.sendDebugMessage(
                        ChatColor.RED.toString() + "Damaging (Magic) x " + ChatColor.DARK_RED + mageMultiplier + ChatColor.RED + " to " + ChatColor.BLUE + targetEntity.type + ": " + damage,
                        5
                    )
                    CompatibilityLib.getCompatibilityUtils().magicDamage(targetEntity, damage, mage.entity)
                }

                if (this.damageType != null && !this.damageType!!.isEmpty()) {
                    val typeDescription = context.controller.messages
                        .get("damage_types." + this.damageType, this.damageType)
                    context.addMessageParameter("damage_type", typeDescription)
                }

                if (this.setLastDamaged) {
                    CompatibilityLib.getCompatibilityUtils().setLastDamaged(targetEntity, mage.entity)
                }

                if (this.setLastDamagedBy) {
                    CompatibilityLib.getCompatibilityUtils().setLastDamagedBy(targetEntity, mage.entity)
                }
            } finally {
                if (this.knockbackResistance != null && livingTarget != null) {
                    val knockBackAttribute = livingTarget.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)
                    knockBackAttribute!!.baseValue = previousKnockbackResistance
                }
                if (this.element != null) {
                    entity.removeMetadata("element", Man10MythicMagic.plugin)
                }
                if (this.killMessage != null) {
                    entity.removeMetadata("killMessage", Man10MythicMagic.plugin)
                }
            }

            return if (this.cancelOnKillTarget && targetEntity.isDead) {
                SpellResult.STOP
            } else {
                SpellResult.CAST
            }
        } else {
            return SpellResult.NO_TARGET
        }
    }

    override fun isUndoable(): Boolean {
        return false
    }

    override fun getParameterNames(spell: Spell?, parameters: MutableCollection<String?>) {
        super.getParameterNames(spell, parameters)
        parameters.add("damage")
        parameters.add("player_damage")
        parameters.add("entity_damage")
        parameters.add("elemental_damage")
        parameters.add("magic_damage")
        parameters.add("percentage")
    }

    override fun getParameterOptions(spell: Spell?, parameterKey: String, examples: MutableCollection<String?>) {
        if ((parameterKey != "damage") && (parameterKey != "player_damage") && (parameterKey != "entity_damage") && (parameterKey != "elemental_damage")) {
            when (parameterKey) {
                "magic_damage" -> {
                    examples.addAll(listOf<String?>(*BaseSpell.EXAMPLE_BOOLEANS))
                }
                "percentage" -> {
                    examples.addAll(listOf<String?>(*BaseSpell.EXAMPLE_PERCENTAGES))
                }
                else -> {
                    super.getParameterOptions(spell, parameterKey, examples)
                }
            }
        } else {
            examples.addAll(listOf<String?>(*BaseSpell.EXAMPLE_SIZES))
        }
    }

    override fun requiresTargetEntity(): Boolean {
        return true
    }
}