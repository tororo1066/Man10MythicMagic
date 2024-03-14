package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.Spell
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.util.Vector
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.nmsutils.SPlayer
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.sItem.SItem
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ThrowItem : CompoundAction() {

    lateinit var material: Material
    private var customModelData = 0
    private var canPick = true
    private var time = 20
    private var yVelocity = 0.0
    private var multiply = 1.0
    private var rotate = 45
    lateinit var context: CastContext
    private var enchant = false

    override fun addHandlers(spell: Spell?, parameters: ConfigurationSection?) {
        addHandler(spell,"actions")
    }

    override fun start(context: CastContext?): SpellResult {
        context?:return SpellResult.FAIL
        this.context = context
        val loc = (context.location?:return SpellResult.FAIL).add(0.0,1.0,0.0)
        loc.pitch = 0f
        val direction = loc.direction
        val theta = (1 - 2 * Random.nextDouble())*3.14/(360/rotate)
        val newX = direction.x * cos(theta) - direction.z * sin(theta)
        val newZ = direction.x * sin(theta) + direction.z * cos(theta)
        val item = SItem(material).setCustomModelData(customModelData).setDisplayName(Random.nextDouble(10000000.0).toString())
        if (enchant) item.setEnchantment(Enchantment.LUCK,1)
        val dropItem = loc.world.dropItem(loc,item)
        dropItem.setCanMobPickup(false)
        if (canPick){
            dropItem.pickupDelay = 20
        }else{
            dropItem.setCanPlayerPickup(false)
        }

        dropItem.velocity = Vector(newX,direction.y+(Random.nextDouble()* yVelocity),newZ).multiply(multiply)
        val event = SEvent(Man10MythicMagic.plugin).register(PlayerAttemptPickupItemEvent::class.java) { e ->
            if (e.item.uniqueId != dropItem.uniqueId) return@register
            SPlayer.getSPlayer(e.player).pickUpPlayer(e.item)
            e.item.remove()
            e.isCancelled = true
            context.targetEntity = e.player
            getHandler("actions")?.cast(context,context.workingParameters)
        }
        Bukkit.getScheduler().runTaskLater(Man10MythicMagic.plugin, Runnable {
            dropItem.remove()
            event.unregister()
        },time.toLong())

        return SpellResult.CAST
    }


    override fun prepare(context: CastContext?, parameters: ConfigurationSection?) {
        context?:return
        parameters?:return
        material = Material.getMaterial(parameters.getString("material","STONE")!!) ?: Material.STONE
        customModelData = parameters.getInt("cmd",0)
        canPick = parameters.getBoolean("canPick",false)
        time = parameters.getInt("time",20)
        multiply = parameters.getDouble("multiply",1.0)
        yVelocity = parameters.getDouble("yVelocity",0.0)
        rotate = parameters.getInt("rotate",45)
        enchant = parameters.getBoolean("enchant",false)
    }

}