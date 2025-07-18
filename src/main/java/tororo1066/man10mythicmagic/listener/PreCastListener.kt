package tororo1066.man10mythicmagic.listener

import com.elmakers.mine.bukkit.api.event.PreCastEvent
import com.elmakers.mine.bukkit.utility.ConfigurationUtils
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.magic.trigger.UltimateTrigger
import tororo1066.man10mythicmagic.utils.getAllWands
import tororo1066.man10mythicmagic.utils.getHotbarWands
import tororo1066.tororopluginapi.SDebug.Companion.sendDebug
import tororo1066.tororopluginapi.sEvent.SEvent
import java.util.UUID
import kotlin.math.max

class PreCastListener {

    companion object {
        val disabledPlayers = HashMap<UUID, Long>() // UUID, remaining time
    }

    val messageCooldown = HashMap<UUID, Long>()

    fun config() = Man10MythicMagic.plugin.config

    private fun getDisabledSpells(section: ConfigurationSection): List<String> {
        val inheritSpells = section.getString("inherit_spells")
        return if (inheritSpells == null) {
            section.getStringList("spells")
        } else {
            getDisabledSpells(config().getConfigurationSection("weapon_disabler.$inheritSpells")?:return emptyList())
        }
    }

    private fun getDisabledWands(section: ConfigurationSection): List<String> {
        val inheritWands = section.getString("inherit_wands")
        return if (inheritWands == null) {
            section.getStringList("wands")
        } else {
            getDisabledWands(config().getConfigurationSection("weapon_disabler.$inheritWands")?:return emptyList())
        }
    }

    init {

        Bukkit.getScheduler().runTaskTimer(Man10MythicMagic.plugin, Runnable {
            disabledPlayers.forEach { (uuid, time) ->
                disabledPlayers[uuid] = max(0, time - 1)
            }
            disabledPlayers.entries.removeIf { it.value <= 0 }
        }, 0, 1)

        SEvent(Man10MythicMagic.plugin).register<PreCastEvent> { e ->
            val player = e.mage.player?:return@register
            if (disabledPlayers.containsKey(player.uniqueId)) {
                e.isCancelled = true
                return@register
            }
        }

        //武器制限
        SEvent(Man10MythicMagic.plugin).register<PreCastEvent> { e ->
            val player = e.mage.player?:return@register
            if (!config().getBoolean("weapon_disabler.enabled"))return@register
            val section = config().getConfigurationSection("weapon_disabler.${player.world.name}").let {
                it ?: config().getConfigurationSection("weapon_disabler.default") ?: return@register
            }
            val disabledSpells = getDisabledSpells(section)
            val disabledWands = getDisabledWands(section)

            val wands = listOfNotNull(e.mage.activeWand, e.mage.offhandWand)
            wands.forEach { wand ->
                if (disabledWands.any { it.toRegex().matches(wand.template?.key?:"") }) {
                    e.isCancelled = true
                    return@register
                }
            }

            if (disabledSpells.any { it.toRegex().matches(e.spell.key) }) {
                e.isCancelled = true
            }
        }

        //武器個数制限
        SEvent(Man10MythicMagic.plugin).register<PreCastEvent> { e ->
            val player = e.mage.player?:return@register
            player.sendDebug(6, "PreCastEvent")
            if (!Man10MythicMagic.plugin.config.getBoolean("weapon_limit.enabled"))return@register
            player.sendDebug(6, "Enabled")
            if (Man10MythicMagic.plugin.config.getStringList("weapon_limit.disabled_worlds")
                    .any { it.toRegex().matches(player.world.name) }) return@register
            player.sendDebug(6, "Not Disabled World")
            val weaponLimit = Man10MythicMagic.plugin.config.getInt("weapon_limit.limit")
            player.sendDebug(6, "Limit: $weaponLimit")
            val hotbarOnly = Man10MythicMagic.plugin.config.getBoolean("weapon_limit.hotbar_only")
            player.sendDebug(6, "Hotbar Only: $hotbarOnly")
            val excludeWands = Man10MythicMagic.plugin.config.getStringList("weapon_limit.exclude_wands").map { it.toRegex() }
            val wands = if (hotbarOnly) getHotbarWands(player) else getAllWands(player)
            var weaponCount = 0
            wands.forEach { wand ->
                if (excludeWands.any { it.matches(wand.template?.key?:"") })return@forEach
                weaponCount += wand.template?.getInt("weapon_count", 1)?:1
                player.sendDebug(6, "Wand: ${wand.template?.key} Count: $weaponCount")
            }

            player.sendDebug(6, "Weapon Count: $weaponCount")

            if (weaponCount > weaponLimit){
                e.isCancelled = true
                if (messageCooldown.getOrDefault(player.uniqueId, 0) > System.currentTimeMillis())return@register
                player.sendMessage(
                    (Man10MythicMagic.plugin.config.getString("weapon_limit.message")?:"")
                        .replace("%limit%", weaponLimit.toString())
                        .replace("%count%", weaponCount.toString())
                )
                messageCooldown[player.uniqueId] = System.currentTimeMillis() + 3000
            }
        }

        //グループごとの武器個数制限
        SEvent(Man10MythicMagic.plugin).register(PreCastEvent::class.java){ e ->
            val group = e.mage.activeWand?.template?.getString("group")?:return@register
            if (!Man10MythicMagic.groups.containsKey(group))return@register
            val hotbar = (0..8).toList().mapNotNull { e.mage.inventory?.getItem(it) }
            var hotbarCount = 0
            hotbar.forEach {
                val wand = Man10MythicMagic.magicAPI.controller.getIfWand(it)?:return@forEach
                if ((wand.template?.getString("group")?:return@forEach) != group)return@forEach
                hotbarCount++
            }

            if (Man10MythicMagic.groups[group]!! < hotbarCount){
                e.mage.player?.sendMessage("§c${group}類の武器は${Man10MythicMagic.groups[group]}個までしか使えません")
                e.isCancelled = true
            }
        }

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
                            }
                        }
                    }
                }
            }
        }
    }
}
