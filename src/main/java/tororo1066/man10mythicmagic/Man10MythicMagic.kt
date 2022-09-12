package tororo1066.man10mythicmagic

import com.elmakers.mine.bukkit.action.ActionFactory
import com.elmakers.mine.bukkit.api.magic.MagicAPI
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import tororo1066.ammoplugin.AmmoAPI
import tororo1066.man10mythicmagic.command.MMMCommands
import tororo1066.man10mythicmagic.listener.EquipArmor
import tororo1066.man10mythicmagic.listener.FlyListener
import tororo1066.man10mythicmagic.listener.ItemDestroyListener
import tororo1066.man10mythicmagic.listener.MythicMobDeathListener
import tororo1066.man10mythicmagic.magic.actions.*
import tororo1066.man10mythicmagic.mythicmobs.skills.*
import tororo1066.nmsutils.SNms
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.otherUtils.UsefulUtility


class Man10MythicMagic : SJavaPlugin(UseOption.MySQL), Listener {

    companion object{
        lateinit var plugin : Man10MythicMagic
        lateinit var magicAPI: MagicAPI
        lateinit var mythicMobs: MythicBukkit
        lateinit var util: UsefulUtility
        lateinit var ammoAPI: AmmoAPI
        lateinit var sNms: SNms
        val logWorlds = ArrayList<String>()
        val groups = HashMap<String,Int>()
    }


    override fun onStart() {
        saveDefaultConfig()
        server.pluginManager.registerEvents(this,this)
        plugin = this
        util = UsefulUtility(this)
        reload()
        val magicPlugin = Bukkit.getPluginManager().getPlugin("Magic")
        magicAPI = magicPlugin as MagicAPI
        ammoAPI = AmmoAPI()
        sNms = SNms.newInstance()?:throw UnsupportedOperationException("SNms not supported mc_version ${server.minecraftVersion}.")
        registerActions()
        mythicMobs = MythicBukkit.inst()
        mythicMobs.skillManager.getMechanic("sound").setTargetsCreativePlayers(true)
        MMMCommands()
        FlyListener()
        EquipArmor()
        ItemDestroyListener()
        MythicMobDeathListener()
    }

    fun reload(){
        logWorlds.clear()
        groups.clear()
        logWorlds.addAll(config.getStringList("logWorlds"))
        val configGroups = config.getConfigurationSection("groups")
        configGroups?.getKeys(false)?.forEach {
            groups[it] = configGroups.getInt(it)
        }
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
        ActionFactory.registerActionClass("ThrowArmorStand",ThrowArmorStand::class.java)
        ActionFactory.registerActionClass("CircleParticle",CircleParticle::class.java)
        ActionFactory.registerActionClass("CheckDurability",CheckDurability::class.java)
        ActionFactory.registerActionClass("ChangeWand",ChangeWand::class.java)
        ActionFactory.registerActionClass("LowHealthDmg",LowHealthDmg::class.java)
        ActionFactory.registerActionClass("SetAllowFly",SetAllowFly::class.java)
        ActionFactory.registerActionClass("SetIsFlying",SetIsFlying::class.java)
        ActionFactory.registerActionClass("CheckFood",CheckFood::class.java)
        ActionFactory.registerActionClass("IsEquipWand",IsEquipWand::class.java)
        ActionFactory.registerActionClass("IgnitePlus",IgnitePlus::class.java)
        ActionFactory.registerActionClass("SetCharged",SetCharged::class.java)
        ActionFactory.registerActionClass("ReHold",ReHold::class.java)
        ActionFactory.registerActionClass("Scope",Scope::class.java)
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