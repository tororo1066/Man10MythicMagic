package tororo1066.man10mythicmagic

import com.elmakers.mine.bukkit.action.ActionFactory
import com.elmakers.mine.bukkit.api.magic.MagicAPI
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import io.lumine.xikage.mythicmobs.io.MythicConfig
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.man10mythicmagic.magic.actions.BlockWave
import tororo1066.man10mythicmagic.mythicmobs.skills.ArmorMechanic
import tororo1066.man10mythicmagic.mythicmobs.skills.SetRotation
import java.io.File


class Man10MythicMagic : JavaPlugin() {

    companion object{
        lateinit var plugin : Man10MythicMagic
    }

    override fun onEnable() {
        plugin = this
        registerActions()
    }

    fun getMagicAPI(): MagicAPI? {
        val magicPlugin: Plugin? = Bukkit.getPluginManager().getPlugin("Magic")
        return if (magicPlugin == null || magicPlugin !is MagicAPI) {
            null
        } else magicPlugin
    }

    private fun registerActions(){
        ActionFactory.registerActionClass("BlockWave", BlockWave::class.java)
    }

    @EventHandler
    fun onMechanicLoad(e : MythicMechanicLoadEvent){
        when{
            e.mechanicName.equals("armordamage",true)->{
                e.register(ArmorMechanic(e.config))
            }
            e.mechanicName.equals("setrotationplus",true)->{
                e.register(SetRotation(e.config))
            }
            e.mechanicName.equals("armordamage",true)->{
                e.register(ArmorMechanic(e.config))
            }
        }

    }
}