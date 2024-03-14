package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.util.Vector
import tororo1066.man10mythicmagic.Man10MythicMagic
import java.io.File
import kotlin.math.max

class SelfTeleport(config: MythicLineConfig, file: File): SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), ITargetedEntitySkill {

    private var x: PlaceholderDouble
    private var y: PlaceholderDouble
    private var z: PlaceholderDouble
    private var pitchReset: Boolean

    init {
        isAsyncSafe = false
        this.x = config.getPlaceholderDouble(arrayOf("x","X"),0.0)
        this.y = config.getPlaceholderDouble(arrayOf("y","Y"),0.0)
        this.z = config.getPlaceholderDouble(arrayOf("z","Z"),0.0)
        this.pitchReset = config.getBoolean(arrayOf("pitchreset","reset"),true)
    }

    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): SkillResult {
        val vector = Vector(this.x.get(data),this.y.get(data),this.z.get(data))
        val max = arrayListOf(vector.x,vector.y,vector.z).max().toInt()
        var i = 1
        if (i <= max){
            while (true){
                var loc = target.location
                if (this.pitchReset)loc.pitch = 0f
                loc = loc.add(loc.direction.normalize().multiply(vector.z / max))
                loc = loc.add(loc.direction.normalize().rotate(90.0F).multiply(vector.x / max))
                loc.add(0.0, vector.y / max, 0.0)
                if (BukkitAdapter.adapt(loc).block.isBuildable)return SkillResult.SUCCESS
                target.teleport(loc)
                if (i >= max)break
                i++
            }
        }

        return SkillResult.SUCCESS
    }
}