package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.mobs.MythicMob
import io.lumine.mythic.api.skills.INoTargetSkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.Location
import tororo1066.man10mythicmagic.Man10MythicMagic

class SummonPlusMechanic(config: MythicLineConfig) : SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,config.line,config), INoTargetSkill {
    private val x : Int
    private val y : Int
    private val z : Int
    private val mob : MythicMob
    override fun cast(p0: SkillMetadata): SkillResult {
        val loc = p0.caster.entity.location
        mob.spawn(BukkitAdapter.adapt(Location(BukkitAdapter.adapt(loc.world),loc.x + x,loc.y + y,loc.z + z)),0.0)
        return SkillResult.SUCCESS
    }

    init {
        this.isAsyncSafe = false
        x = config.getInteger("x",0)
        y = config.getInteger("y",0)
        z = config.getInteger("z",0)
        mob = plugin.mobManager.getMythicMob(config.getString(arrayOf("mob","m"))).get()
    }

}