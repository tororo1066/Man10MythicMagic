package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import tororo1066.man10mythicmagic.Man10MythicMagic

class IgnoreDamage: CompoundAction() {

    private var damage: Int = 0

    override fun perform(context: CastContext): SpellResult {
        val entity = context.targetEntity?:return SpellResult.FAIL
        if (entity !is LivingEntity)return SpellResult.FAIL
        if (!Man10MythicMagic.magicAPI.controller.isPVPAllowed(context.mage.player,context.targetLocation)){
            return SpellResult.CAST
        }
        if (entity.health <= damage){
            entity.health = 0.0
        } else {
            entity.health -= damage
        }
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        damage = parameters.getInt("damage")
    }
}