package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.utility.ConfigurationUtils
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import kotlin.math.cos
import kotlin.math.sin

class CircleParticle : CompoundAction() {

    private var points = 10
    private var radius = 3.0
    private var count = 1
    private lateinit var color: Color
    private lateinit var particle: Particle
    private lateinit var offset: Vector


    override fun start(context: CastContext): SpellResult {

        val origin = context.targetLocation?:return SpellResult.FAIL

        for (i in 0 until points) {
            val angle = 2 * Math.PI * i / points
            val point = origin.clone().add(radius * sin(angle), 0.0, radius * cos(angle)).add(offset.toLocation(origin.world))
            origin.world.spawnParticle(particle,point,count,Particle.DustOptions(color,1f))
        }

        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        this.points = parameters.getInt("points",10)
        this.radius = parameters.getDouble("radius",3.0)
        this.count = parameters.getInt("count",1)
        this.particle = UsefulUtility.sTry({Particle.valueOf(parameters.getString("particle", "REDSTONE")!!.toUpperCase())},{Particle.REDSTONE})
        this.color = ConfigurationUtils.getColor(parameters,"color", Color.RED)
        this.offset = ConfigurationUtils.getVector(parameters,"offset")?:Vector(0.0,0.0,0.0)
    }
}