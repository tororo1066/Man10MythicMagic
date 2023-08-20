package tororo1066.man10mythicmagic

import com.elmakers.mine.bukkit.action.ActionFactory
import com.elmakers.mine.bukkit.api.magic.MagicAPI
import com.elmakers.mine.bukkit.effect.EffectLibManager
import com.elmakers.mine.bukkit.slikey.effectlib.EffectLib
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ISkillMechanic
import io.lumine.mythic.api.skills.targeters.ISkillTargeter
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import tororo1066.man10mythicmagic.command.MMMCommands
import tororo1066.man10mythicmagic.listener.*
import tororo1066.man10mythicmagic.magic.actions.*
import tororo1066.man10mythicmagic.mythicmobs.MobDeathLoggerTable
import tororo1066.man10mythicmagic.mythicmobs.skills.*
import tororo1066.man10mythicmagic.mythicmobs.target.LocPlusTarget
import tororo1066.man10mythicmagic.noused.NoUsed
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import java.io.File


class Man10MythicMagic : SJavaPlugin(UseOption.MySQL), Listener {

    companion object{
        lateinit var plugin : Man10MythicMagic
        lateinit var magicAPI: MagicAPI
        lateinit var mythicMobs: MythicBukkit
        var effectLib: EffectLibManager? = null
        lateinit var util: UsefulUtility
        lateinit var mobDeathLoggerTable: MobDeathLoggerTable
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
        if (magicPlugin != null){
            magicAPI = magicPlugin as MagicAPI
            effectLib = EffectLibManager(this)
            registerActions()
            FlyListener()
            ItemDestroyListener()
            DamageListener()
            PlayerLocationTrackListener()
            CancelChargeListener()
            WandActivateListener()
        }
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null){
            mythicMobs = MythicBukkit.inst()
            mobDeathLoggerTable = MobDeathLoggerTable()
            MythicMobDeathListener()
        }

        MMMCommands()

        NoUsed.register()
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

    private fun registerMagic(vararg pair: Pair<String,Class<*>>){
        pair.forEach {
            ActionFactory.registerActionClass(it.first,it.second)
        }
    }

    private fun registerActions(){
        registerMagic(
            "TeleportAndEffect" to TeleportAndEffect::class.java,
            "AddEnchantEffect" to AddEnchantmentEffect::class.java,
            "RemoveEnchantEffect" to RemoveEnchantmentEffect::class.java,
            "CheckEnchantEffect" to CheckEnchantmentEffect::class.java,
            "AmmoReload" to AmmoReload::class.java,
            "CheckPotionPlus" to CheckPotionEffect::class.java,
            "ThrowItemPlus" to ThrowItem::class.java,
            "CallMythicSkill" to CallMythicSkill::class.java,
            "CheckCMD" to CheckCMD::class.java,
            "ThrowArmorStand" to ThrowArmorStand::class.java,
            "CircleParticle" to CircleParticle::class.java,
            "CheckDurability" to CheckDurability::class.java,
            "ChangeWand" to ChangeWand::class.java,
            "LowHealthDmg" to LowHealthDmg::class.java,
            "SetAllowFly" to SetAllowFly::class.java,
            "SetIsFlying" to SetIsFlying::class.java,
            "CheckFood" to CheckFood::class.java,
            "IsEquipWand" to IsEquipWand::class.java,
            "IgnitePlus" to IgnitePlus::class.java,
            "SetCharged" to SetCharged::class.java,
            "ReHold" to ReHold::class.java,
            "Scope" to Scope::class.java,
            "Recoil" to Recoil::class.java,
            "IgnoreDamage" to IgnoreDamage::class.java,
            "ScopingAction" to ScopingAction::class.java,
            "IsOnGround" to IsOnGround::class.java,
            "RecallBackFuture" to RecallBackFuture::class.java,
            "ArmorStandEquip" to ArmorStandEquip::class.java,
        )

    }


    private fun MythicMechanicLoadEvent.registerMechanic(vararg pair: Pair<String, Class<out ISkillMechanic>>){
        pair.forEach {
            if (mechanicName.equals(it.first,true))
                register(it.second.getConstructor(MythicLineConfig::class.java, File::class.java)
                    .newInstance(config, container.file))
        }
    }

    @EventHandler
    fun onMechanicLoad(e : MythicMechanicLoadEvent){
        e.registerMechanic(
            "ARMORDAMAGE" to ArmorMechanic::class.java,
            "SETROTATIONPLUS" to SetRotation::class.java,
            "SUMMONPLUS" to SummonPlusMechanic::class.java,
            "CALLSPELL" to CallMagicSpell::class.java,
            "RADIUSCOMMAND" to RadiusCommandExecute::class.java,
            "SELFTELEPORT" to SelfTeleport::class.java
        )
    }

    private fun MythicTargeterLoadEvent.registerTarget(vararg pair: Pair<String, Class<out ISkillTargeter>>){
        pair.forEach {
            if (targeterName.equals(it.first,true))
                register(it.second.getConstructor(MythicLineConfig::class.java)
                    .newInstance(config))
        }
    }

    @EventHandler
    fun onTargetLoad(e: MythicTargeterLoadEvent){
        e.registerTarget(
            "LOCOFFSET" to LocPlusTarget::class.java
        )
    }




}