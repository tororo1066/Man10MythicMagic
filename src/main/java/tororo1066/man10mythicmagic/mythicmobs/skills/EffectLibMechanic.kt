package tororo1066.man10mythicmagic.mythicmobs.skills

import com.elmakers.mine.bukkit.effect.EffectPlayer
import com.elmakers.mine.bukkit.slikey.effectlib.util.DynamicLocation
import io.lumine.mythic.api.adapters.AbstractLocation
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedLocationSkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.configuration.file.YamlConfiguration
import tororo1066.man10mythicmagic.Man10MythicMagic
import java.io.File

class EffectLibMechanic(config: MythicLineConfig, file: File): SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), ITargetedLocationSkill {

    val effectPlayer: String
    val parameters: Map<String, String>

    override fun castAtLocation(data: SkillMetadata, location: AbstractLocation): SkillResult {
        if (Man10MythicMagic.effectLib == null)return SkillResult.ERROR
        val effectPlayer = Man10MythicMagic.util.sTry({
            Class.forName("com.elmakers.mine.bukkit.effect.builtin.${effectPlayer}")
                .getConstructor().newInstance() as EffectPlayer
        },{ null })?:return SkillResult.ERROR
        Man10MythicMagic.effectLib!!.play(YamlConfiguration().apply {
            parameters.forEach { (key, value) ->
                set(key, value)
            }
        }, effectPlayer, DynamicLocation(BukkitAdapter.adapt(data.origin)), DynamicLocation(BukkitAdapter.adapt(location)),
            null, null)
        return SkillResult.SUCCESS
    }

    init {
        isAsyncSafe = false
        effectPlayer = config.getString("player","EffectSingle")
        parameters = config.entrySet().apply {
            removeIf { it.key == "player" }
        }.associate { it.key to it.value }
    }
}