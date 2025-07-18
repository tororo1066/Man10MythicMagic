package tororo1066.man10mythicmagic.listener

import net.kyori.adventure.text.Component
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

            val killer = e.player.killer ?: return@register

            val replace = killMessage
                .replace("<target.name>", e.player.name)
                .replace("<caster.name>", killer.name)

            e.deathMessage(Component.text(replace))
        }
    }
}