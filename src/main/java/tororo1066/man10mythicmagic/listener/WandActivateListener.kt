package tororo1066.man10mythicmagic.listener

import com.elmakers.mine.bukkit.api.event.PreCastEvent
import com.elmakers.mine.bukkit.api.event.WandActivatedEvent
import com.elmakers.mine.bukkit.api.event.WandDeactivatedEvent
import com.elmakers.mine.bukkit.api.event.WandPreActivateEvent
import com.elmakers.mine.bukkit.api.wand.Wand
import com.elmakers.mine.bukkit.api.wand.WandAction
import com.elmakers.mine.bukkit.spell.BaseSpell
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.man10mythicmagic.packet.IDualWeapon
import tororo1066.man10mythicmagic.packet.VersionHandler
import tororo1066.man10mythicmagic.utils.getAllWandSpells
import tororo1066.man10mythicmagic.utils.getAllWands
import tororo1066.man10mythicmagic.utils.getHotbarWands
import tororo1066.tororopluginapi.SDebug.Companion.sendDebug
import tororo1066.tororopluginapi.sEvent.SEvent
import java.util.UUID
import kotlin.math.max

class WandActivateListener {

    companion object {
        private val beforeWandMap = HashMap<UUID, Wand>()
    }

    init {

        SEvent(Man10MythicMagic.plugin).register<WandActivatedEvent> { e ->
            if (Man10MythicMagic.protocolManager == null)return@register
            val template = e.wand.template?:return@register
            if (template.getBoolean("dual_weapon")) {
                val player = e.mage.player?:return@register
                if (!player.inventory.itemInOffHand.type.isAir) return@register
                VersionHandler.getInstance(IDualWeapon::class.java).sendPacket(player, e.wand.item?:return@register)
            }
        }

        SEvent(Man10MythicMagic.plugin).register<WandDeactivatedEvent> { e ->
            if (Man10MythicMagic.protocolManager == null)return@register
            val template = e.wand.template?:return@register
            if (template.getBoolean("dual_weapon")) {
                VersionHandler.getInstance(IDualWeapon::class.java).sendResetPacket(e.mage.player?:return@register)
            }
        }

        SEvent(Man10MythicMagic.plugin).register<WandActivatedEvent> { e ->
            e.mage.player?.sendDebug(6, "WandActivatedEvent")

            val beforeWand = beforeWandMap[e.mage.entity.uniqueId]
            val beforeTemplate = beforeWand?.template

            beforeWandMap[e.mage.entity.uniqueId] = e.wand

            e.mage.player?.sendDebug(6, "Before: ${beforeTemplate?.key}")
            e.mage.player?.sendDebug(6, "After: ${e.wand.template?.key}")

            val template = e.wand.template?:return@register
            e.mage.player?.sendDebug(6, "Template: ${template.key}")

            val afterEnabled = beforeTemplate != null && beforeTemplate.getBoolean(
                "weapon_switch.after.enabled", true
            ) && Man10MythicMagic.plugin.config.getStringList("weapon_switch.after.disabled").none {
                it.toRegex().matches(beforeTemplate.key)
            }

            val beforeEnabled = template.getBoolean("weapon_switch.before.enabled", true) && Man10MythicMagic.plugin.config.getStringList("weapon_switch.before.disabled").none {
                it.toRegex().matches(template.key)
            }

            if (!afterEnabled && !beforeEnabled)return@register

            val afterWeaponSwitchTime =
                if (afterEnabled) max(beforeTemplate!!.getLong(
                    "weapon_switch.after.time",
                    Man10MythicMagic.plugin.config.getLong("weapon_switch.after.time", 0)
                ), 0)
                else 0
            val beforeWeaponSwitchTime =
                if (beforeEnabled) max(template.getLong(
                    "weapon_switch.before.time",
                    Man10MythicMagic.plugin.config.getLong("weapon_switch.before.time", 0)
                ), 0)
                else 0

            e.mage.player?.sendDebug(6, "Before: $beforeWeaponSwitchTime")
            e.mage.player?.sendDebug(6, "After: $afterWeaponSwitchTime")

            val afterExclude = (beforeTemplate?.getStringList("weapon_switch.after.exclude") ?:
                Man10MythicMagic.plugin.config.getStringList("weapon_switch.after.exclude"))
                .map { it.toRegex() }
            val beforeExclude = (template.getStringList("weapon_switch.before.exclude") ?:
                Man10MythicMagic.plugin.config.getStringList("weapon_switch.before.exclude"))
                .map { it.toRegex() }

            val beforeModified = arrayListOf<String>()

            if (beforeWeaponSwitchTime > 0) {
                getAllWandSpells(e.wand, e.mage).forEach { spell ->
                    if (spell !is BaseSpell)return@forEach
                    if (beforeExclude.any { it.matches(spell.key) })return@forEach
                    if (spell.remainingCooldown < beforeWeaponSwitchTime) {
                        spell.remainingCooldown = beforeWeaponSwitchTime
                        e.wand.item?.let {
                            e.mage.player?.setCooldown(it.type, (beforeWeaponSwitchTime/20).toInt())
                        }
                        beforeModified.add(spell.key)
                    }
                    e.mage.player?.sendDebug(3, "Weapon switch cooldown ${spell.remainingCooldown}(Spell: ${spell.key}, before)")
                }
            }

            if (afterWeaponSwitchTime > 0) {
                getAllWands(e.mage.player?:return@register).forEach first@ { wand ->
                    e.mage.player?.sendDebug(6, "Wand: ${wand.template?.key}")
                    getAllWandSpells(wand, e.mage).forEach { spell ->
                        e.mage.player?.sendDebug(6, "Spell: ${spell.key}(after)")
                        if (spell !is BaseSpell)return@forEach
                        if (afterExclude.any { it.matches(spell.key) })return@forEach
                        if (beforeModified.contains(spell.key) && afterWeaponSwitchTime < beforeWeaponSwitchTime)return@forEach
                        if (spell.remainingCooldown < afterWeaponSwitchTime) {
                            spell.remainingCooldown = afterWeaponSwitchTime
                            wand.item?.let {
                                e.mage.player?.setCooldown(it.type, (afterWeaponSwitchTime/20).toInt())
                            }
                        }
                        e.mage.player?.sendDebug(3, "Weapon switch cooldown ${spell.remainingCooldown}(Spell: ${spell.key}, after)")
                    }
                }
            }
        }

        SEvent(Man10MythicMagic.plugin).register(WandActivatedEvent::class.java){ e ->
            val template = e.wand.template?:return@register
            val onHold = template.getString("on_hold")?:return@register
            e.wand.performAction(WandAction.valueOf(onHold.uppercase()))
        }
    }
}