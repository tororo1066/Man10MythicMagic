package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CheckAction
import com.elmakers.mine.bukkit.api.action.CastContext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

class CheckPotionEffect : CheckAction() {

    lateinit var effect: PotionEffectType
    var level = 0
    override fun isAllowed(p0: CastContext?): Boolean {
        p0?:return false
        val entity = (p0.targetEntity?:return false)
        if (entity !is Player)return false

        return (entity.getPotionEffect(effect)?:return false).amplifier == level
    }

    override fun prepare(context: CastContext?, parameters: ConfigurationSection?) {
        context?:return
        parameters?:return
        effect = PotionEffectType.getByName(parameters.getString("type","slow")!!)!!
        level = parameters.getInt("level",0)
    }
}