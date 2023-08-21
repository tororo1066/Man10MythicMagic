package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.SDebug
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.utils.addY
import tororo1066.tororopluginapi.utils.setPitchL
import java.util.*

class TeleportAndEffect : CompoundAction() {

    private var length = 3
    private val particles = ArrayList<Particle>()
    private var yOffSet = 0.0
    private var radius = 0.0

    private var nowLength = 0
    private var y = .0


    private val damagedEntities = ArrayList<UUID>()



    override fun reset(context: CastContext?) {
        SDebug.broadcastDebug(2, "reset")
        super.reset(context)
        nowLength = 0
        y = .0
        damagedEntities.clear()
    }

    override fun next(context: CastContext): Boolean {
        nowLength++
        SDebug.broadcastDebug(2, "damagedEntities:$damagedEntities")
        return nowLength <= length
    }

    override fun step(context: CastContext): SpellResult {
        val p = context.entity as? Player?:return SpellResult.FAIL
        val playerLoc = context.location!!
        playerLoc.pitch = 0f
        val blockTest1 = p.world.rayTraceBlocks(playerLoc,playerLoc.direction,1.5,FluidCollisionMode.NEVER,true)
        val blockTest2 = p.world.rayTraceBlocks(playerLoc.clone().addY(1.0),playerLoc.direction,1.5,FluidCollisionMode.NEVER,true)
        if ((blockTest1 != null && blockTest1.hitBlock != null) || (blockTest2 != null && blockTest2.hitBlock != null)){
            return SpellResult.NO_ACTION
        }

        var loc = (context.location)!!.add(playerLoc.direction.multiply(1))
        loc = Location(loc.world,loc.x,y,loc.z,loc.yaw,loc.pitch)
        if (loc.block.isBuildable || loc.addY(1.0).block.isBuildable)return SpellResult.CAST
        p.teleport(loc)
        particles.forEach {
            loc.world.spawnParticle(it,loc.set(loc.x,y+yOffSet,loc.z),1)
        }

        if (radius == 0.0)return SpellResult.CAST
        loc.getNearbyLivingEntities(radius).forEach {
            if (!damagedEntities.contains(it.uniqueId) && context.entity!!.uniqueId != it.uniqueId){
                val clone = com.elmakers.mine.bukkit.action.CastContext(context)
                clone.targetEntity = it
                SDebug.broadcastDebug(1, "casting:${it.uniqueId}")
                getHandler("actions")!!.cast(clone,clone.workingParameters)
                damagedEntities.add(it.uniqueId)
            }
        }

        return SpellResult.CAST
    }

    override fun start(context: CastContext): SpellResult {
        context.location?:return SpellResult.FAIL
        if (context.location!!.block.isBuildable || context.location!!.add(0.0,1.0,0.0).block.isBuildable)return SpellResult.CAST
        if (radius != 0.0){
            context.location!!.getNearbyLivingEntities(radius).forEach {
                if (!damagedEntities.contains(it.uniqueId) && context.entity!!.uniqueId != it.uniqueId){
                    val clone = com.elmakers.mine.bukkit.action.CastContext(context)
                    clone.targetEntity = it
                    getHandler("actions")!!.cast(clone,clone.workingParameters)
                    damagedEntities.add(it.uniqueId)
                }
            }
        }

        y = context.location!!.y

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
    }
}