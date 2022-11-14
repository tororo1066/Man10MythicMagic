package tororo1066.man10mythicmagic.mythicmobs.target

import io.lumine.mythic.api.adapters.AbstractLocation
import io.lumine.mythic.api.adapters.AbstractVector
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.targeters.ILocationTargeter
import io.lumine.mythic.core.skills.SkillTargeter
import tororo1066.man10mythicmagic.Man10MythicMagic

class LocPlusTarget(config: MythicLineConfig): SkillTargeter(Man10MythicMagic.mythicMobs.skillManager,config), ILocationTargeter {

    val offset = ArrayList<AbstractVector>()

    override fun getLocations(meta: SkillMetadata): MutableCollection<AbstractLocation> {
        val list = ArrayList<AbstractLocation>()
        offset.forEach {
            var loc = meta.caster.location.clone()
            loc.pitch = 0f
            loc = loc.add(loc.direction.normalize().multiply(it.z))
            loc = loc.add(loc.direction.normalize().rotate(90f).multiply(it.x))
            loc = loc.add(0.0,it.y,0.0)
            list.add(loc)
        }

        return list
    }

    init {
        val list = config.getString("offset").split("_").map { it.split(",").map { it2-> it2.toDouble() } }
        list.forEach {
            offset.add(AbstractVector(it[0],it[1],it[2]))
        }
    }
}