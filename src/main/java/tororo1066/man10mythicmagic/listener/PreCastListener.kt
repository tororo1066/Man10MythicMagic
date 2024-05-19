package tororo1066.man10mythicmagic.listener

import com.elmakers.mine.bukkit.api.event.PreCastEvent
import com.elmakers.mine.bukkit.utility.ConfigurationUtils
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.magic.trigger.UltimateTrigger
import tororo1066.tororopluginapi.sEvent.SEvent

class PreCastListener {

    init {
        SEvent(Man10MythicMagic.plugin).register(PreCastEvent::class.java) { e ->
            val spell = e.spell
            val node = spell.configuration
            if (node.isList("triggers")) {
                val list = node.getList("triggers")!!
                if (list.isNotEmpty()) {
                    val triggersConfiguration = ConfigurationUtils.getNodeList(node, "triggers")
                    if (triggersConfiguration != null && triggersConfiguration.isNotEmpty()) {
                        for (triggerConfiguration in triggersConfiguration) {
                            val ultimate = triggerConfiguration.getBoolean("ultimate", false)
                            if (ultimate) {
                                val lastDamageEntity = triggerConfiguration.getString("damage_entity_type")?.let { EntityType.valueOf(it.uppercase()) }
                                if (lastDamageEntity != null && UltimateTrigger.lastDamageEntities.containsKey(e.mage.entity.uniqueId)) {
                                    val entity = UltimateTrigger.lastDamageEntities[e.mage.entity.uniqueId]
                                    if (entity?.type != lastDamageEntity) {
                                        e.mage.isQuiet = true
                                        e.isCancelled = true
                                        Bukkit.getScheduler().runTask(Man10MythicMagic.plugin, Runnable {
                                            e.mage.isQuiet = false
                                        })
                                    }
                                }

                                if (triggerConfiguration.getBoolean("get_target", false)) {
                                    if (triggerConfiguration.getString("trigger") !in listOf("damage_dealt","kill")) {
                                        return@register
                                    }

                                    if (spell.workingParameters.isSet("entity")) {
                                        return@register
                                    }

                                    val target = UltimateTrigger.lastDamageEntities[e.mage.entity.uniqueId]
                                    if (target != null) {
                                        e.spell.cast(ConfigurationUtils.newConfigurationSection().also {
                                            it.set("entity", target.uniqueId.toString())
                                        })
                                    }
                                    e.mage.isQuiet = true
                                    e.isCancelled = true
                                    Bukkit.getScheduler().runTask(Man10MythicMagic.plugin, Runnable {
                                        e.mage.isQuiet = false
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
