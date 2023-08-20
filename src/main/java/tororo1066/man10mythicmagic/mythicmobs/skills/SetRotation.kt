package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.INoTargetSkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.core.skills.SkillMechanic
import tororo1066.man10mythicmagic.Man10MythicMagic
import java.io.File

class SetRotation(config : MythicLineConfig, file: File) : SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), INoTargetSkill {
    private val yaw : Float
    private val pitch : Float
    override fun cast(p0: SkillMetadata): SkillResult {
        if (yaw == -1000f){
            p0.caster.entity.bukkitEntity.setRotation(p0.caster.entity.bukkitEntity.location.yaw,pitch)
            return SkillResult.SUCCESS
        }
        if (pitch == -1000f){
            p0.caster.entity.bukkitEntity.setRotation(yaw,p0.caster.entity.bukkitEntity.location.pitch)
            return SkillResult.SUCCESS
        }
        p0.caster.entity.bukkitEntity.setRotation(yaw,pitch)
        return SkillResult.SUCCESS
    }

    init {
        this.isAsyncSafe = false
        yaw = config.getFloat(arrayOf("yaw","y"),-1000f)
        pitch = config.getFloat(arrayOf("pitch","p"),-1000f)
    }
}