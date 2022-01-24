package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata

class SetRotation(config : MythicLineConfig) : SkillMechanic(config.line,config), INoTargetSkill {
    private val yaw : Float
    private val pitch : Float
    override fun cast(p0: SkillMetadata?): Boolean {
        if (p0 == null)return false
        if (yaw == -1000f){
            p0.caster.entity.bukkitEntity.setRotation(p0.caster.entity.bukkitEntity.location.yaw,pitch)
            return true
        }
        if (pitch == -1000f){
            p0.caster.entity.bukkitEntity.setRotation(yaw,p0.caster.entity.bukkitEntity.location.pitch)
            return true
        }
        p0.caster.entity.bukkitEntity.setRotation(yaw,pitch)
        return true
    }

    init {
        this.isAsyncSafe = true
        yaw = config.getFloat(arrayOf("yaw","y"),-1000f)
        pitch = config.getFloat(arrayOf("pitch","p"),-1000f)
    }
}