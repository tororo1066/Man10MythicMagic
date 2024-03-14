package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.ticxo.playeranimator.api.PlayerAnimator
import com.ticxo.playeranimator.api.model.player.PlayerModel
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.EquipmentSlot
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.nmsutils.SPlayer

class PlayerAnimation: CompoundAction() {

    private lateinit var animName: String

    override fun start(context: CastContext): SpellResult {
        val p = context.mage.player?:return SpellResult.FAIL
        SPlayer.getSPlayer(p).invisibleItems(EquipmentSlot.values().toList(), true)
        p.isInvisible = true
        val model = PlayerModel(p)
        model.playAnimation(animName)
        Bukkit.getScheduler().runTaskLater(Man10MythicMagic.plugin, Runnable {
            model.despawn()
            PlayerAnimator.api.modelManager.unregisterModel(model)
            p.isInvisible = false
            SPlayer.getSPlayer(p).invisibleItems(EquipmentSlot.values().toList(), false)
        }, (model.animationProperty.animation.length*20).toLong())
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        animName = parameters.getString("animName")?:""
    }
}