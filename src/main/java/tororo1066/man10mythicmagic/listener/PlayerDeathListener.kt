package tororo1066.man10mythicmagic.listener

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.entity.PlayerDeathEvent
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sEvent.SEvent

class PlayerDeathListener {

    init {
        SEvent(Man10MythicMagic.plugin).register<PlayerDeathEvent> { e ->
            val killMessage = e.player.getMetadata("killMessage")
                .firstOrNull {
                    it.owningPlugin == Man10MythicMagic.plugin
                }?.asString() ?: return@register
            val killedBy = e.player.getMetadata("killedBy")
                .firstOrNull {
                    it.owningPlugin == Man10MythicMagic.plugin
                }?.asString() ?: return@register

            val killer = Bukkit.getPlayer(killedBy)

            val replace = killMessage
                .replace("<target.name>", e.player.name)
                .replace("<caster.name>", killer?.name ?: "Unknown")

            e.deathMessage(Component.text(replace))
        }
    }
}