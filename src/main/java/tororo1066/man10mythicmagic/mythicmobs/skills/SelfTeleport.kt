package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.util.Vector
import tororo1066.man10mythicmagic.Man10MythicMagic
import java.io.File
import kotlin.math.max

class SelfTeleport(config: MythicLineConfig, file: File): SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), ITargetedEntitySkill {

    private var vector: Vector
    private var pitchReset: Boolean

    init {
        isAsyncSafe = false

        val split = config.getString(arrayOf("vector","vec"),"3,0,3").split(",")
            .map { it.toDouble() }
        this.vector = Vector(split[0],split[1],split[2])
        this.pitchReset = config.getBoolean(arrayOf("pitchreset","reset"),true)
    }

    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): SkillResult {
        val max = arrayListOf(vector.x,vector.y,vector.z).max().toInt()
        var i = 1
        if (i <= max){
            while (true){
                var loc = target.location
                if (this.pitchReset)loc.pitch = 0f
                loc = loc.add(loc.direction.normalize().multiply(this.vector.z / max))
                loc = loc.add(loc.direction.normalize().rotate(90.0F).multiply(this.vector.x / max))
                loc.add(0.0, this.vector.y / max, 0.0)
                if (BukkitAdapter.adapt(loc).block.isBuildable)return SkillResult.SUCCESS
                target.teleport(loc)
                if (i >= max)break
                i++
            }
        }

        return SkillResult.SUCCESS
    }
}