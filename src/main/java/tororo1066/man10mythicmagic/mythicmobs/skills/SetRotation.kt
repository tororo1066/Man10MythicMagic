package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.INoTargetSkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.Bukkit
import tororo1066.man10mythicmagic.Man10MythicMagic
import java.io.File

class SetRotation(config : MythicLineConfig, file: File) : SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), INoTargetSkill {
    private val yaw : PlaceholderFloat
    private val pitch : PlaceholderFloat
    override fun cast(p0: SkillMetadata): SkillResult {
        val yaw = yaw.get(p0)
        val pitch = pitch.get(p0)
        if (yaw == -1000f && pitch == -1000f)return SkillResult.SUCCESS
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
        yaw = config.getPlaceholderFloat(arrayOf("yaw","y"),-1000f)
        pitch = config.getPlaceholderFloat(arrayOf("pitch","p"),-1000f)
    }
}