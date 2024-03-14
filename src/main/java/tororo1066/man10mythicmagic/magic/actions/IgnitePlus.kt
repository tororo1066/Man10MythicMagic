package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.Man10MythicMagic

class IgnitePlus: CompoundAction() {

    var duration = 1000

    override fun start(context: CastContext): SpellResult {
        val entity = context.targetEntity?:return SpellResult.FAIL
        if (entity !is LivingEntity)return SpellResult.FAIL
        if (context.targetEntity is Player && !Man10MythicMagic.magicAPI.controller.isPVPAllowed(context.mage.player,context.targetLocation)){
            return SpellResult.CAST
        }
        entity.fireTicks = duration/50
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        duration = parameters.getInt("duration",1000)
    }
}