package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.EulerAngle
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sItem.SItem
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThrowArmorStand : CompoundAction() {

    lateinit var type: Material
    var cmd = 0
    lateinit var loc: Location
    lateinit var rotation: EulerAngle
    var yOffSet = 0.0
    var deleteDelay = 0
    private val es: ExecutorService = Executors.newCachedThreadPool()

    override fun start(context: CastContext): SpellResult {
        this.loc.world = context.world
        val spawnLoc = context.location!!.clone()
        spawnLoc.y += yOffSet
        spawnLoc.yaw = 0f
        spawnLoc.pitch = 0f
        context.entity!!.world.spawn(spawnLoc,ArmorStand::class.java) {
            it.setAI(false)
            it.setGravity(false)
            it.setArms(true)
            it.addEquipmentLock(EquipmentSlot.HAND,ArmorStand.LockType.REMOVING_OR_CHANGING)
            it.isInvisible = true
            it.isInvulnerable = true
            it.noDamageTicks = 0
            it.setItem(EquipmentSlot.HAND,SItem(type).setCustomModelData(cmd))
            it.rightArmPose = rotation

            es.execute {
                while (!it.location.block.isBuildable){
                    Bukkit.getScheduler().runTask(Man10MythicMagic.plugin, Runnable {
                        it.teleport(it.location.add(loc))
                    })
                    Thread.sleep(50)
                }
                Bukkit.getScheduler().runTask(Man10MythicMagic.plugin, Runnable {
                    val cloneContext = com.elmakers.mine.bukkit.action.CastContext(context)
                    cloneContext.targetEntity = it
                    cloneContext.targetLocation = it.location
                    getHandler("actions")?.cast(cloneContext,cloneContext.workingParameters)
                })

                Thread.sleep(deleteDelay * 50L)

                Bukkit.getScheduler().runTask(Man10MythicMagic.plugin, Runnable {
                    it.remove()
                })
            }
        }
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        this.type = Material.getMaterial(parameters.getString("type","STONE")!!.toUpperCase())!!
        this.cmd = parameters.getInt("cmd")
        val locSplit = (parameters.getString("tpLoc")?:"0,0,0").split(",")
        if (locSplit.size != 3)throw NullPointerException("tpLoc must <x>,<y>,<z>")
        this.loc = Location(null,locSplit[0].toDouble(),locSplit[1].toDouble(),locSplit[2].toDouble())
        val rotationSplit = (parameters.getString("rotation")?:"0,0,0").split(",")
        if (rotationSplit.size != 3)throw NullPointerException("rotation must <x>,<y>,<z>")
        this.rotation = EulerAngle(rotationSplit[0].toDouble(),rotationSplit[1].toDouble(),rotationSplit[2].toDouble())
        this.deleteDelay = parameters.getInt("deleteDelay")
        this.yOffSet = parameters.getDouble("yOffSet")
    }
}