package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import java.util.*

class TeleportAndEffect : CompoundAction() {

    private var length = 3
    private val particles = ArrayList<Particle>()
    private var yOffSet = 0.0
    private var radius = 0.0


    private val damagedPlayers = ArrayList<UUID>()



    override fun reset(context: CastContext?) {
        damagedPlayers.clear()
    }

    override fun start(context: CastContext?): SpellResult {
        context?:return SpellResult.FAIL
        context.location?:return SpellResult.FAIL
        if (context.location!!.block.isBuildable || context.location!!.add(0.0,1.0,0.0).block.isBuildable)return SpellResult.CAST
        if (radius != 0.0){
            context.location!!.getNearbyEntitiesByType(Player::class.java, radius).forEach {
                if (!damagedPlayers.contains(it.uniqueId) && context.entity!!.uniqueId != it.uniqueId){
                    context.targetEntity = it
                    getHandler("actions")!!.cast(context,context.workingParameters)
                    damagedPlayers.add(it.uniqueId)
                }
            }
        }

        val y = context.location!!.y
        for (to in 1..length){

            val playerLoc = context.location!!
            playerLoc.pitch = 0f
            var loc = (context.location)!!.add(playerLoc.direction.multiply(1))
            loc = Location(loc.world,loc.x,y,loc.z,loc.yaw,loc.pitch)
            if (!loc.block.isBuildable && !loc.add(0.0,1.0,0.0).block.isBuildable){
                (context.entity?:return SpellResult.FAIL).teleport(loc)
                particles.forEach {
                    loc.world.spawnParticle(it,loc.set(loc.x,y+yOffSet,loc.z),1)
                }

                if (radius == 0.0)continue
                loc.getNearbyEntitiesByType(Player::class.java, radius).forEach {
                    if (!damagedPlayers.contains(it.uniqueId) && context.entity!!.uniqueId != it.uniqueId){
                        context.targetEntity = it
                        getHandler("actions")!!.cast(context,context.workingParameters)
                        damagedPlayers.add(it.uniqueId)
                    }
                }
            } else break
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
    }
}