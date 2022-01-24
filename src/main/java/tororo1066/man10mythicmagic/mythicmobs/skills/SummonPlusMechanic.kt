package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.mobs.MythicMob
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import org.bukkit.Location

class SummonPlusMechanic(config: MythicLineConfig) : SkillMechanic(config.line,config), INoTargetSkill {
    private val x : Int
    private val y : Int
    private val z : Int
    private val mob : MythicMob
    override fun cast(p0: SkillMetadata?): Boolean {
        if (p0 == null)return false
        val loc = p0.caster.entity.location
        mob.spawn(BukkitAdapter.adapt(Location(BukkitAdapter.adapt(loc.world),loc.x + x,loc.y + y,loc.z + z)),0.0)
        return true
    }

    init {
        this.isAsyncSafe = false
        x = config.getInteger("x",0)
        y = config.getInteger("y",0)
        z = config.getInteger("z",0)
        mob = getPlugin().mobManager.getMythicMob(config.getString(arrayOf("mob","m")))
    }

}