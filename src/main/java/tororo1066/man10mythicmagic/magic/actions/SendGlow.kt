package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.nmsutils.SEntity
import tororo1066.nmsutils.items.GlowColor

class SendGlow: CompoundAction() {

    var glow = true
    var color: GlowColor = GlowColor.WHITE
    var onlySource = false

    override fun perform(context: CastContext): SpellResult {
        val target = context.targetEntity ?: return SpellResult.NO_TARGET
        val sEntity = SEntity.getSEntity(target)
        val receivers = if (onlySource) listOfNotNull(context.entity as? Player) else context.mage.location.world?.players ?: emptyList()

        sEntity.sendGlow(glow, receivers, color)

        if (!glow) {
            sEntity.setTeam(color.getTeamName(), receivers)
        }

        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        glow = parameters.getBoolean("glow", true)
        color = GlowColor.entries.firstOrNull { it.name.equals(parameters.getString("color"), true) } ?: GlowColor.WHITE
        onlySource = parameters.getBoolean("onlySource", false)
    }
}