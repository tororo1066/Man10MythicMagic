package tororo1066.man10mythicmagic

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.elmakers.mine.bukkit.action.ActionFactory
import com.elmakers.mine.bukkit.api.magic.MagicAPI
import com.elmakers.mine.bukkit.effect.EffectLibManager
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
import tororo1066.man10mythicmagic.magic.trigger.UltimateTrigger
import tororo1066.man10mythicmagic.mythicmobs.MobDeathLoggerTable
import tororo1066.man10mythicmagic.mythicmobs.skills.*
import tororo1066.man10mythicmagic.mythicmobs.target.LocPlusTarget
import tororo1066.man10mythicmagic.noused.NoUsed
import tororo1066.man10mythicmagic.packet.IDualWeapon
import tororo1066.man10mythicmagic.packet.VersionHandler
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

        var protocolManager: ProtocolManager? = null

        var foundMagic = false
        var foundMythic = false
    }


    override fun onStart() {
        saveDefaultConfig()
        plugin = this
        util = UsefulUtility(this)
        reload()
        val magicPlugin = Bukkit.getPluginManager().getPlugin("Magic")
        val mythicPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs")
        if (magicPlugin != null) foundMagic = true
        if (mythicPlugin != null) foundMythic = true

        if (foundMagic){
            magicAPI = magicPlugin as MagicAPI
            effectLib = EffectLibManager(this)
            registerActions()
            FlyListener()
            ItemDestroyListener()
            DamageListener()
            PlayerLocationTrackListener()
            CancelChargeListener()
            WandActivateListener()
            ScopeListener()
            PreCastListener()
            PlayerDeathListener()
            UltimateTrigger
//            CustomPotionManager.load()

            if (Bukkit.getPluginManager().getPlugin("PlayerAnimator") != null){
                registerMagic("PlayerAnim" to PlayerAnimation::class.java)
            }
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null){
                VersionHandler.getInstance(IDualWeapon::class.java).listenPacket()
                protocolManager = ProtocolLibrary.getProtocolManager()
            }
        }
        if (foundMythic){
            server.pluginManager.registerEvents(this,this)
            mythicMobs = MythicBukkit.inst()
            mobDeathLoggerTable = MobDeathLoggerTable()
            MythicMobDeathListener()
            MythicMobDamageListener()
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
            "CheckPvPAllowed" to CheckPvPAllowed::class.java,
            "FakeItem" to FakeItem::class.java,
            "ModifyWandLore" to ModifyWandLore::class.java,
            "BackStab" to BackStab::class.java,
            "ModifyPropertiesPlus" to ModifyPropertiesPlus::class.java,
            "UniqueVariable" to UniqueVariable::class.java,
            "CustomPotionEffect" to CustomPotionEffect::class.java,
            "CheckCustomPotionEffect" to CheckCustomPotionEffect::class.java,
            "RestoreCustomPotionEffectInfo" to RestoreCustomPotionEffectInfo::class.java,
            "DamagePlus" to DamagePlus::class.java,
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
            "RADIUSCOMMAND" to RadiusCommandExecute::class.java,
            "SELFTELEPORT" to SelfTeleport::class.java
        )
        if (foundMagic) {
            e.registerMechanic(
                "EFFECTLIB" to EffectLibMechanic::class.java,
                "CALLSPELL" to CallMagicSpell::class.java,
                "DISABLEMAGICSPELLS" to DisableMagicSpells::class.java,
            )
        }
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