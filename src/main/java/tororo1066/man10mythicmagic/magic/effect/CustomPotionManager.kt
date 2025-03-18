package tororo1066.man10mythicmagic.magic.effect

import com.elmakers.mine.bukkit.configuration.MagicConfiguration
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.SJavaPlugin
import java.io.File
import java.util.UUID

object CustomPotionManager: Listener {

    val customPotionEffects = HashMap<String, CustomPotionEffect>()
    val customPotionEffectInstances = HashMap<UUID, HashMap<String, ArrayList<CustomPotionEffectInstance>>>()
    val queues = ArrayDeque<() -> Unit>()

    fun load() {
        customPotionEffects.clear()
        customPotionEffectInstances.clear()
        val folder = File(SJavaPlugin.plugin.dataFolder, "effects")
        if (!folder.exists()) folder.mkdirs()
        val files = folder.listFiles() ?: return
        for (file in files) {
            if (file.extension != "yml") continue
            val config = YamlConfiguration.loadConfiguration(file)
            config.getKeys(false).forEach { key ->
                val section = config.getConfigurationSection(key) ?: return@forEach
                val effect = CustomPotionEffect()
                effect.initialize(Man10MythicMagic.magicAPI.controller)
                effect.loadTemplate(key, MagicConfiguration.getKeyed(
                    Man10MythicMagic.magicAPI.controller, section, key
                ))
                customPotionEffects[key] = effect
            }
        }
    }

    fun addPotionEffect(entity: Entity, effect: String, duration: Int, amplifier: Int, player: UUID? = null) {
        queues.add {
            val customEffect = (customPotionEffects[effect] ?: return@add).createMageSpell(Man10MythicMagic.magicAPI.controller.getMage(entity)) as CustomPotionEffect
            val instance = CustomPotionEffectInstance(entity, customEffect, duration, amplifier, player)
            instance.add()
        }
    }

    fun removePotionEffect(entity: Entity, effect: String, player: UUID? = null) {
        queues.add {
            customPotionEffectInstances[entity.uniqueId]?.get(effect)?.removeAll {
                if (it.player == player) {
                    it.remove(delete = false)
                    true
                } else {
                    false
                }
            }
        }
    }

    init {
        Bukkit.getPluginManager().registerEvents(this, SJavaPlugin.plugin)
        load()

        Bukkit.getScheduler().runTaskTimer(SJavaPlugin.plugin, Runnable {
            queues.forEach { it() }
            queues.clear()
            customPotionEffectInstances.forEach { (_, effects) ->
                effects.forEach second@ { (_, instances) ->
                    val max = instances.maxByOrNull { it.amplifier } ?: return@second
                    var fired = false
                    instances.forEach {
                        if (it.amplifier == max.amplifier && !fired && !it.shouldRemove) {
                            it.tick()
                            fired = true
                        } else {
                            it.tick(false)
                        }
                    }
                }
            }
            customPotionEffectInstances.forEach { (_, effects) ->
                effects.forEach { (_, instances) ->
                    instances.removeAll {
                        if (it.shouldRemove) {
                            it.remove(delete = false)
                            true
                        } else {
                            false
                        }
                    }
                }
            }
        }, 0, 1)
    }

    @EventHandler
    fun onDeath(e: EntityDeathEvent) {
        queues.add {
            customPotionEffectInstances[e.entity.uniqueId]?.forEach { (_, instances) ->
                instances.removeAll {
                    if (it.effect.removeOnDeath) {
                        it.remove(delete = false)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }
}