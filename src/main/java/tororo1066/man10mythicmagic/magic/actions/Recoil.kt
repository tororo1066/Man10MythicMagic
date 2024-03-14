package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.nmsutils.SPlayer
import kotlin.random.Random

class Recoil: CompoundAction() {

    var minYaw = 0.0
    var maxYaw = 0.0
    var minPitch = 0.0
    var maxPitch = 0.0

    override fun perform(context: CastContext): SpellResult {
        val target = context.targetEntity?:return SpellResult.NO_TARGET
        if (target !is Player)return SpellResult.NO_TARGET
        SPlayer.getSPlayer(target).moveRotation(-Random.nextDouble(minYaw, maxYaw).toFloat(), -Random.nextDouble(minPitch, maxPitch).toFloat())
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        minYaw = parameters.getDouble("yaw.min")
        maxYaw = parameters.getDouble("yaw.max")
        minPitch = parameters.getDouble("pitch.min")
        maxPitch = parameters.getDouble("pitch.max")
    }
}