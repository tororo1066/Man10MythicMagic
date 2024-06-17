package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.block.DefaultMaterials
import com.elmakers.mine.bukkit.spell.BaseSpell
import com.elmakers.mine.bukkit.utility.CompatibilityLib
import com.elmakers.mine.bukkit.utility.ConfigurationUtils
import com.elmakers.mine.bukkit.utility.SafetyUtils
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector
import java.util.Arrays

class ModifyBlockFix: CompoundAction() {
    private var spawnFallingBlocks = false
    private var fallingBlocksHurt = false
    private var fallingBlockSpeed = 0.0
    private var fallingBlockDirection: Vector? = null
    private var fallingBlockFallDamage = 0f
    private var fallingBlockMaxDamage = 0
    private var fallingProbability = 0.0
    private var breakable = 0.0
    private var backfireChance = 0.0
    private var applyPhysics = false
    private var commit = false
    private var consumeBlocks = false
    private var consumeVariants = true
    private var checkChunk = false
    private var autoBlockState = false
    private var replaceSame = false

    override fun prepare(context: CastContext?, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        this.spawnFallingBlocks = parameters.getBoolean("falling", false)
        this.applyPhysics = parameters.getBoolean("physics", false)
        this.autoBlockState = parameters.getBoolean("auto_block_state", false)
        this.commit = parameters.getBoolean("commit", false)
        this.breakable = parameters.getDouble("breakable", 0.0)
        this.backfireChance = parameters.getDouble("reflect_chance", 0.0)
        this.fallingBlockSpeed = parameters.getDouble("speed", 0.0)
        this.fallingProbability = parameters.getDouble("falling_probability", 1.0)
        this.consumeBlocks = parameters.getBoolean("consume", false)
        this.consumeVariants = parameters.getBoolean("consume_variants", true)
        this.fallingBlocksHurt = parameters.getBoolean("falling_hurts", false)
        this.checkChunk = parameters.getBoolean("check_chunk", true)
        this.replaceSame = parameters.getBoolean("replace_same", false)
        this.fallingBlockDirection = null
        if (this.spawnFallingBlocks && parameters.contains("direction") && parameters.getString("direction")!!.isNotEmpty()
        ) {
            if (this.fallingBlockSpeed == 0.0) {
                this.fallingBlockSpeed = 1.0
            }

            this.fallingBlockDirection = ConfigurationUtils.getVector(parameters, "direction")
        }

        val damage = parameters.getInt("damage", 0)
        this.fallingBlockFallDamage = parameters.getDouble("fall_damage", damage.toDouble()).toFloat()
        this.fallingBlockMaxDamage = parameters.getInt("max_damage", damage)
    }

    override fun perform(context: CastContext): SpellResult {
        val brush = context.brush
        if (brush == null) {
            return SpellResult.FAIL
        } else if (this.checkChunk && !CompatibilityLib.getCompatibilityUtils().checkChunk(context.targetLocation)) {
            context.addWork(100)
            return SpellResult.PENDING
        } else {
            val block = context.targetBlock
            if (brush.isErase) {
                if (!context.hasBreakPermission(block)) {
                    return SpellResult.INSUFFICIENT_PERMISSION
                }
            } else if (!context.hasBuildPermission(block)) {
                return SpellResult.INSUFFICIENT_PERMISSION
            }

            if (this.commit) {
                if (!context.areAnyDestructible(block)) {
                    return SpellResult.NO_TARGET
                }
            } else if (!context.isDestructible(block)) {
                return SpellResult.NO_TARGET
            }

            var fallingMaterial: Material? = block!!.type
            var fallingData = CompatibilityLib.getCompatibilityUtils().getBlockData(block)
            var fallingLegacyData = block.data
            val mage = context.mage
            brush.update(mage, context.targetSourceLocation)
            if (!brush.isReady) {
                brush.prepare()
                return SpellResult.PENDING
            } else if (!brush.isValid) {
                return SpellResult.FAIL
            } else if (!brush.isTargetValid) {
                return SpellResult.NO_TARGET
            } else if (!this.replaceSame && !brush.isDifferent(block)) {
                return SpellResult.NO_TARGET
            } else {
                if (this.consumeBlocks && !context.isConsumeFree && !brush.isErase) {
                    val undoList = context.undoList
                    if (undoList != null) {
                        undoList.isConsumed = true
                    }

                    if (!mage.consumeBlock(brush, this.consumeVariants)) {
                        val requiresMessage = context.getMessage("insufficient_resources")
                        context.sendMessageKey("insufficient_resources", requiresMessage.replace("\$cost", brush.name))
                        return SpellResult.INSUFFICIENT_RESOURCES
                    }
                }

                var spawnFalling = this.spawnFallingBlocks
                if (spawnFalling && this.fallingProbability < 1.0) {
                    spawnFalling = context.random.nextDouble() < this.fallingProbability
                }

                if (spawnFalling && !brush.isErase) {
                    fallingMaterial = brush.material
                    fallingData = brush.modernBlockData
                    val data = brush.blockData
                    fallingLegacyData = data ?: 0
                } else {
                    if (!this.commit) {
                        context.registerForUndo(block)
                        if (brush.isErase && !DefaultMaterials.isAir(block.type)) {
                            context.clearAttachables(block)
                        }
                    }

                    val undoList = context.undoList
                    if (undoList != null) {
                        undoList.applyPhysics = applyPhysics
                    }

                    val prior = block.state
                    brush.modify(block, this.applyPhysics)
                    if (undoList != null && !undoList.isScheduled) {
                        context.controller.logBlockChange(context.mage, prior, block.state)
                    }

                    if (this.autoBlockState) {
                        val targetLocation = context.targetLocation
                        val hitBlock = targetLocation!!.block
                        var direction = hitBlock.getFace(block)
                        if (direction == BlockFace.SELF) {
                            direction = BlockFace.UP
                        }

                        CompatibilityLib.getCompatibilityUtils().setAutoBlockState(
                            block, targetLocation, direction,
                            this.applyPhysics, context.mage.player
                        )
                    }
                }

                spawnFalling = spawnFalling && !DefaultMaterials.isAir(fallingMaterial)
                if (spawnFalling) {
                    val blockLocation = block.location
                    val blockCenter = Location(
                        blockLocation.world,
                        blockLocation.x + 0.5,
                        blockLocation.y + 0.5,
                        blockLocation.z + 0.5
                    )
                    var fallingBlockVelocity: Vector? = null
                    if (this.fallingBlockSpeed > 0.0) {
                        val source = context.targetCenterLocation
                        fallingBlockVelocity = blockCenter.clone().subtract(source!!).toVector()
                        fallingBlockVelocity.normalize()
                        if (this.fallingBlockDirection != null) {
                            fallingBlockVelocity.add(fallingBlockDirection!!).normalize()
                        }

                        fallingBlockVelocity.multiply(this.fallingBlockSpeed)
                    }

                    if (fallingBlockVelocity != null && (java.lang.Double.isNaN(fallingBlockVelocity.x) || java.lang.Double.isNaN(
                            fallingBlockVelocity.y
                        ) || java.lang.Double.isNaN(fallingBlockVelocity.z) || java.lang.Double.isInfinite(
                            fallingBlockVelocity.x
                        ) || java.lang.Double.isInfinite(fallingBlockVelocity.y) || java.lang.Double.isInfinite(
                            fallingBlockVelocity.z
                        ))
                    ) {
                        fallingBlockVelocity = null
                    }
                    val falling = if (fallingData != null) {
                        CompatibilityLib.getCompatibilityUtils()
                            .spawnFallingBlock(blockCenter, fallingMaterial, fallingData)
                    } else {
                        CompatibilityLib.getDeprecatedUtils()
                            .spawnFallingBlock(blockCenter, fallingMaterial, fallingLegacyData)
                    }

                    falling.dropItem = false
                    if (fallingBlockVelocity != null) {
                        SafetyUtils.setVelocity(falling, fallingBlockVelocity)
                    }

                    if (this.fallingBlockMaxDamage > 0 && this.fallingBlockFallDamage > 0.0f) {
                        CompatibilityLib.getCompatibilityUtils().setFallingBlockDamage(
                            falling,
                            this.fallingBlockFallDamage,
                            this.fallingBlockMaxDamage
                        )
                    } else {
                        falling.setHurtEntities(this.fallingBlocksHurt)
                    }

                    context.registerForUndo(falling)
                }

                if (this.breakable > 0.0) {
                    context.registerBreakable(block, this.breakable)
                }

                if (this.backfireChance > 0.0) {
                    context.registerReflective(block, this.backfireChance)
                }

                if (this.commit) {
                    val blockData = context.undoList[block]
                    blockData.commit()
                }

                return SpellResult.CAST
            }
        }
    }

    override fun getParameterNames(spell: Spell?, parameters: MutableCollection<String?>) {
        super.getParameterNames(spell, parameters)
        parameters.add("falling")
        parameters.add("speed")
        parameters.add("direction")
        parameters.add("reflect_chance")
        parameters.add("breakable")
        parameters.add("physics")
        parameters.add("commit")
        parameters.add("hurts")
    }

    override fun getParameterOptions(spell: Spell?, parameterKey: String, examples: MutableCollection<String?>) {
        if (parameterKey != "falling" && parameterKey != "physics" && parameterKey != "commit" && parameterKey != "falling_hurts") {
            if (parameterKey != "speed" && parameterKey != "breakable") {
                when (parameterKey) {
                    "direction" -> {
                        examples.addAll(listOf(*BaseSpell.EXAMPLE_VECTOR_COMPONENTS))
                    }
                    "reflect_chance" -> {
                        examples.addAll(listOf(*BaseSpell.EXAMPLE_PERCENTAGES))
                    }
                    else -> {
                        super.getParameterOptions(spell, parameterKey, examples)
                    }
                }
            } else {
                examples.addAll(listOf(*BaseSpell.EXAMPLE_SIZES))
            }
        } else {
            examples.addAll(listOf(*BaseSpell.EXAMPLE_BOOLEANS))
        }
    }

    override fun requiresBuildPermission(): Boolean {
        return true
    }

    override fun requiresTarget(): Boolean {
        return true
    }

    override fun isUndoable(): Boolean {
        return true
    }

    override fun usesBrush(): Boolean {
        return true
    }
}