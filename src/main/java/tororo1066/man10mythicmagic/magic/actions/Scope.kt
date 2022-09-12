package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.api.wand.Wand
import com.elmakers.mine.bukkit.block.MaterialAndData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffectType
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.sItem.SItem

class Scope: CompoundAction() {

    companion object{
        fun unScopePlayer(p: Player, wand: Wand){
            if (!p.hasMetadata("magic_scope"))return
            wand.inactiveIcon
            wand.icon = MaterialAndData(SItem(wand.icon.material!!).setCustomModelData(p.getMetadata("magic_scope")[0].asInt()))
            p.removePotionEffect(PotionEffectType.SPEED)
            p.removeMetadata("magic_scope",Man10MythicMagic.plugin)
            wand.saveState()
        }
    }

    var level = 1
    var scopeCmd = 0
    var unScopeCmd = 0


    override fun perform(context: CastContext): SpellResult {
        val p = (context.targetEntity as? Player)?:return SpellResult.FAIL
        val wand = context.wand?:return SpellResult.FAIL
        if (p.hasMetadata("magic_scope")){
            unScopePlayer(p,wand)
            return SpellResult.CAST
        }
        wand.icon = MaterialAndData(SItem(wand.icon.material!!).setCustomModelData(scopeCmd))
        wand.saveState()
        p.setMetadata("magic_scope",FixedMetadataValue(Man10MythicMagic.plugin,unScopeCmd))
        p.removePotionEffect(PotionEffectType.SPEED)
        p.addPotionEffect(PotionEffectType.SPEED.createEffect(2400,-level))
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        level = parameters.getInt("level",1)
        scopeCmd = parameters.getInt("onScope",0)
        unScopeCmd = parameters.getInt("onUnScope",0)
    }
}