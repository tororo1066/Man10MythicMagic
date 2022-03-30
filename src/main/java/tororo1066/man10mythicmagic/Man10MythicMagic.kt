package tororo1066.man10mythicmagic

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.elmakers.mine.bukkit.action.ActionFactory
import com.elmakers.mine.bukkit.api.magic.MagicAPI
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.man10mythicmagic.command.MMMCommands
import tororo1066.man10mythicmagic.magic.actions.*
import tororo1066.man10mythicmagic.mythicmobs.skills.*
import java.util.*


class Man10MythicMagic : JavaPlugin(), Listener {

    companion object{
        lateinit var plugin : Man10MythicMagic
        val flyingBlocks = ArrayList<UUID>()
        lateinit var protocolManager: ProtocolManager
        lateinit var magicAPI: MagicAPI
        lateinit var mythicMobs: MythicMobs
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        plugin = this
        protocolManager = ProtocolLibrary.getProtocolManager()
        val magicPlugin = Bukkit.getPluginManager().getPlugin("Magic")
        magicAPI = magicPlugin as MagicAPI
        registerActions()
        MMMCommands()
        mythicMobs = MythicMobs.inst()
        mythicMobs.skillManager.getSkillMechanic("sound").setTargetsCreativePlayers(true)
    }



    private fun registerActions(){
        ActionFactory.registerActionClass("BlockWave", BlockWave::class.java)
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
    }

    @EventHandler
    fun onChangeBlock(e : EntityChangeBlockEvent){
        if (flyingBlocks.contains(e.entity.uniqueId)){
            e.isCancelled = true
            flyingBlocks.remove(e.entity.uniqueId)
        }
    }




}