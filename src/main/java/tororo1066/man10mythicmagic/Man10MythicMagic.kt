package tororo1066.man10mythicmagic

import com.elmakers.mine.bukkit.action.ActionFactory
import com.elmakers.mine.bukkit.api.magic.MagicAPI
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.man10mythicmagic.command.MMMCommands
import tororo1066.man10mythicmagic.magic.actions.*
import tororo1066.man10mythicmagic.mythicmobs.skills.*


class Man10MythicMagic : JavaPlugin(), Listener {

    companion object{
        lateinit var plugin : Man10MythicMagic
        lateinit var magicAPI: MagicAPI
        lateinit var mythicMobs: MythicBukkit
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        plugin = this
        val magicPlugin = Bukkit.getPluginManager().getPlugin("Magic")
        magicAPI = magicPlugin as MagicAPI
        registerActions()
        mythicMobs = MythicBukkit.inst()
        mythicMobs.skillManager.getMechanic("sound").setTargetsCreativePlayers(true)
        MMMCommands()
    }



    private fun registerActions(){
        ActionFactory.registerActionClass("TeleportAndEffect",TeleportAndEffect::class.java)
        ActionFactory.registerActionClass("AddEnchantEffect",AddEnchantmentEffect::class.java)
        ActionFactory.registerActionClass("RemoveEnchantEffect",RemoveEnchantmentEffect::class.java)
        ActionFactory.registerActionClass("CheckEnchantEffect",CheckEnchantmentEffect::class.java)
        ActionFactory.registerActionClass("AmmoReload",AmmoReload::class.java)
        ActionFactory.registerActionClass("CheckPotionPlus",CheckPotionEffect::class.java)
        ActionFactory.registerActionClass("ThrowItemPlus",ThrowItem::class.java)
        ActionFactory.registerActionClass("CallMythicSkill",CallMythicSkill::class.java)
        ActionFactory.registerActionClass("CheckCMD",CheckCMD::class.java)

    }

    @EventHandler
    fun onMechanicLoad(e : MythicMechanicLoadEvent){
        if (e.mechanicName.equals("ARMORDAMAGE",true)) e.register(ArmorMechanic(e.config))
        if (e.mechanicName.equals("SETROTATIONPLUS",true)) e.register(SetRotation(e.config))
        if (e.mechanicName.equals("SUMMONPLUS",true)) e.register(SummonPlusMechanic(e.config))
        if (e.mechanicName.equals("CALLSPELL",true)) e.register(CallMagicSpell(e.config))
        if (e.mechanicName.equals("RADIUSCOMMAND",true)) e.register(RadiusCommandExecute(e.config))
    }




}