package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import io.papermc.paper.entity.TeleportFlag
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.SDebug
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.utils.LocType
import tororo1066.tororopluginapi.utils.addY
import tororo1066.tororopluginapi.utils.setPitchL
import tororo1066.tororopluginapi.utils.toLocString
import java.util.*

class TeleportAndEffect : CompoundAction() {

    private var length = 3
    private val particles = ArrayList<Particle>()
    private var yOffSet = 0.0
    private var radius = 0.0
    private var relative = false

    private var nowLength = 0
    private var y = .0
    private var end = false


    private val damagedEntities = ArrayList<UUID>()


    override fun addHandlers(spell: Spell?, parameters: ConfigurationSection?) {
        super.addHandlers(spell, parameters)
        addHandler(spell, "step")
    }

    override fun reset(context: CastContext?) {
        super.reset(context)
        nowLength = 0
        y = .0
        end = false
        damagedEntities.clear()
    }

    private fun end(context: CastContext) {
        val loc = (context.location?:return).setPitchL(0f)
        val teleportLocation = loc.clone().add(loc.clone().direction.multiply(nowLength))
        if (relative) {
            context.entity?.teleport(
                teleportLocation.setPitchL(context.location!!.pitch),
                TeleportFlag.Relative.X,
                TeleportFlag.Relative.Y,
                TeleportFlag.Relative.Z,
                TeleportFlag.Relative.YAW,
                TeleportFlag.Relative.PITCH
            )
        } else {
            context.entity?.teleport(teleportLocation.setPitchL(context.location!!.pitch))
        }
    }

    override fun next(context: CastContext): Boolean {
        if (nowLength <= length && !end){
            nowLength++
            return true
        }
        end(context)
        return false
    }



    override fun step(context: CastContext): SpellResult {
        val p = context.entity as? Player?:return SpellResult.FAIL
        val playerLoc = context.location!!
        playerLoc.pitch = 0f

        var loc = playerLoc.add(playerLoc.direction.clone().multiply(nowLength))

        val leftLoc = loc.clone().add(loc.direction.clone().rotateAroundY(90.0).multiply(0.5))
        val rightLoc = loc.clone().add(loc.direction.clone().rotateAroundY(90.0).multiply(-0.5))
        val centerLoc = loc.clone()
        val leftResult1 = p.world.rayTraceBlocks(leftLoc,loc.direction,1.5,FluidCollisionMode.NEVER,true)
        val leftResult2 = p.world.rayTraceBlocks(leftLoc.clone().addY(1.0),loc.direction,1.5,FluidCollisionMode.NEVER,true)
        val rightResult1 = p.world.rayTraceBlocks(rightLoc, loc.direction,1.5,FluidCollisionMode.NEVER,true)
        val rightResult2 = p.world.rayTraceBlocks(rightLoc.clone().addY(1.0),loc.direction,1.5,FluidCollisionMode.NEVER,true)
        val centerResult1 = p.world.rayTraceBlocks(centerLoc,loc.direction,1.5,FluidCollisionMode.NEVER,true)
        val centerResult2 = p.world.rayTraceBlocks(centerLoc.clone().addY(1.0),loc.direction,1.5,FluidCollisionMode.NEVER,true)


        if ((leftResult1 != null && leftResult1.hitBlock != null) || (leftResult2 != null && leftResult2.hitBlock != null)
                || (rightResult1 != null && rightResult1.hitBlock != null) || (rightResult2 != null && rightResult2.hitBlock != null)
                || (centerResult1 != null && centerResult1.hitBlock != null) || (centerResult2 != null && centerResult2.hitBlock != null)){
//            end(context)
            end = true
            return SpellResult.CAST
        }
        loc = Location(loc.world,loc.x,y,loc.z,loc.yaw,loc.pitch)
            .add(loc.direction.clone().multiply(1.0))
        if (loc.block.isBuildable || loc.clone().addY(1.0).block.isBuildable)return SpellResult.NO_ACTION
//        p.teleport(loc)
        particles.forEach {
            loc.world.spawnParticle(it,loc.clone().set(loc.x,y+yOffSet,loc.z),1)
        }

        createActionContext(context,context.entity,context.location,null,loc)
        startActions("step")

        if (radius == 0.0)return SpellResult.CAST
        loc.getNearbyLivingEntities(radius).forEach {
            if (!damagedEntities.contains(it.uniqueId) && context.entity!!.uniqueId != it.uniqueId){
                createActionContext(context,context.entity,context.location,it,it.location)
                startActions()
                damagedEntities.add(it.uniqueId)
            }
        }

        return SpellResult.CAST
    }

    override fun start(context: CastContext): SpellResult {
        context.location?:return SpellResult.FAIL
        y = context.location!!.y
        if (context.location!!.block.isBuildable || context.location!!.add(0.0,1.0,0.0).block.isBuildable)return SpellResult.NO_ACTION
        if (radius != 0.0){
            context.location!!.getNearbyLivingEntities(radius).forEach {
                if (!damagedEntities.contains(it.uniqueId) && context.entity!!.uniqueId != it.uniqueId){
                    createActionContext(context,context.entity,context.location,it,it.location)
                    startActions()
                    damagedEntities.add(it.uniqueId)
                }
            }
        }

        return SpellResult.CAST
    }

    override fun prepare(context: CastContext?, parameters: ConfigurationSection?) {
        context?:return
        parameters?:return
        particles.clear()
        length = parameters.getInt("length",3)

        UsefulUtility.sTry({
            parameters.getStringList("particles").forEach {
                particles.add(Particle.valueOf(it))
            }
        }, {
            it.printStackTrace()
        })
        radius = parameters.getDouble("radius",0.0)
        yOffSet = parameters.getDouble("yoffset",0.0)
        relative = parameters.getBoolean("relative",false)
    }
}