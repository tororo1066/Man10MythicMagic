package tororo1066.man10mythicmagic.hud

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.customhudapi.hud.service.CustomHudServices
import tororo1066.customhudapi.hud.service.IHudService
import tororo1066.man10mythicmagic.Man10MythicMagic

internal object CustomPotionEffectRendererRegistration : Listener {

    private var hudService: IHudService? = null
    private lateinit var plugin: Man10MythicMagic

    fun start(plugin: Man10MythicMagic) {
        if (Bukkit.getPluginManager().getPlugin("CustomHud") == null) return
        val hudConfig = plugin.config.getConfigurationSection("custom-potion-effect-hud") ?: return
        if (!hudConfig.getBoolean("enabled", true)) return

        this.plugin = plugin
        hudService = try {
            CustomHudServices.require()
        } catch (exception: IllegalStateException) {
            plugin.logger.warning("CustomHud is installed but its HUD service is unavailable: ${exception.message}")
            return
        }
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.onlinePlayers.forEach(::show)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        show(event.player)
    }

    fun stop(plugin: Man10MythicMagic) {
        val service = hudService ?: return
        plugin.server.onlinePlayers.forEach { service.remove(plugin, it, "custom-potion-effects") }
        hudService = null
    }

    private fun show(player: Player) {
        val service = hudService ?: return
        val config = plugin.config.getConfigurationSection("custom-potion-effect-hud") ?: return
        service.put(plugin, player, CustomPotionEffectRenderer.fromConfig(player, config))
    }
}
