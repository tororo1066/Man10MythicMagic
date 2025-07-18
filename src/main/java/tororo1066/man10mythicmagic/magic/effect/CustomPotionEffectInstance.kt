package tororo1066.man10mythicmagic.magic.effect

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import java.util.UUID

class CustomPotionEffectInstance(
    val entity: Entity,
    var effect: CustomPotionEffect,
    val duration: Int,
    val amplifier: Int,
    val player: UUID? = null
) {

    val instanceId = UUID.randomUUID()
    var currentTick = 0
    var shouldRemove = false

//    init {
//        effect = effect.createMageSpell(Man10MythicMagic.magicAPI.controller.getMage(entity)) as CustomPotionEffect
//    }

    fun add() {
        val instances = CustomPotionManager.customPotionEffectInstances.computeIfAbsent(entity.uniqueId) { HashMap() }
        val effects = instances.computeIfAbsent(effect.name) { ArrayList() }
        if (effects.isNotEmpty()) {
            effects.forEach {
                //amplifierが同じ以上かつ、durationが長い場合は追加しない
                if (it.amplifier >= amplifier
                    && it.duration - it.currentTick > duration - currentTick
                    && it.instanceId != instanceId
                    && ((it.player == null && player == null) || it.player == player)) {
                    return
                }

                //amplifierがそれより小さく、durationが短い場合は削除
                if (it.amplifier < amplifier
                    && it.duration - it.currentTick < duration - currentTick
                    && it.instanceId != instanceId
                    && ((it.player == null && player == null) || it.player == player)) {
                    it.shouldRemove = true
                }
            }
        }
        effects.removeAll {
            if (it.shouldRemove) {
                it.remove(
                    delete = false,
                    handler = it.effect.castRemoveOnOverride
                )
                true
            } else {
                false
            }
        }
        effects.add(this)
        effect.cast(handlerParameters("add"))
    }

    fun tick(handler: Boolean = true) {
        currentTick++

        if (handler && currentTick % effect.tickInterval == 0) {
            effect.cast(handlerParameters("tick"))
        }

        if (currentTick >= duration) {
//            remove()
            shouldRemove = true
        }
    }

    fun remove(delete: Boolean = true, handler: Boolean = true) {
        if (handler) {
            effect.cast(handlerParameters("remove"))
        }
        if (delete) {
            CustomPotionManager.customPotionEffectInstances[entity.uniqueId]?.get(effect.name)?.remove(this)
        }
    }

    fun handlerParameters(handler: String): ConfigurationSection {
        return YamlConfiguration().apply {
            set("handler", handler)
            set("amplifier", amplifier)
            set("duration", duration)
            set("grantor", player)
        }
    }
}