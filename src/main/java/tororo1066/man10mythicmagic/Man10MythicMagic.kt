package tororo1066.man10mythicmagic

import com.elmakers.mine.bukkit.action.ActionFactory
import com.elmakers.mine.bukkit.api.magic.MagicAPI
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.man10mythicmagic.command.MMMCommands
import tororo1066.man10mythicmagic.magic.actions.*
import tororo1066.man10mythicmagic.mythicmobs.skills.ArmorMechanic
import tororo1066.man10mythicmagic.mythicmobs.skills.SetRotation
import tororo1066.man10mythicmagic.mythicmobs.skills.SummonPlusMechanic
import java.util.UUID


class Man10MythicMagic : JavaPlugin(), Listener {

    companion object{
        lateinit var plugin : Man10MythicMagic
        val flyingBlocks = ArrayList<UUID>()
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        plugin = this
        registerActions()
        MMMCommands()
    }

    fun getMagicAPI(): MagicAPI? {
        val magicPlugin: Plugin? = Bukkit.getPluginManager().getPlugin("Magic")
        return if (magicPlugin == null || magicPlugin !is MagicAPI) {
            null
        } else magicPlugin
    }

    private fun registerActions(){
        ActionFactory.registerActionClass("BlockWave", BlockWave::class.java)
        ActionFactory.registerActionClass("TeleportAndEffect",TeleportAndEffect::class.java)
        ActionFactory.registerActionClass("AddEnchantEffect",AddEnchantmentEffect::class.java)
        ActionFactory.registerActionClass("RemoveEnchantEffect",RemoveEnchantmentEffect::class.java)
        ActionFactory.registerActionClass("CheckEnchantEffect",CheckEnchantmentEffect::class.java)
        ActionFactory.registerActionClass("AmmoReload",AmmoReload::class.java)
    }

    @EventHandler
    fun onMechanicLoad(e : MythicMechanicLoadEvent){
        if (e.mechanicName.equals("ARMORDAMAGE",true)) e.register(ArmorMechanic(e.config))
        if (e.mechanicName.equals("SETROTATIONPLUS",true)) e.register(SetRotation(e.config))
        if (e.mechanicName.equals("SUMMONPLUS",true)) e.register(SummonPlusMechanic(e.config))
    }

    @EventHandler
    fun onChangeBlock(e : EntityChangeBlockEvent){
        if (flyingBlocks.contains(e.entity.uniqueId)){
            e.isCancelled = true
            flyingBlocks.remove(e.entity.uniqueId)
        }
    }

}