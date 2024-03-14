package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import io.papermc.paper.entity.TeleportFlag
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.tasks.PlayerLocationTrackTask

class RecallBackFuture: CompoundAction() {
    private var recallTime = 100
    private var relative = false

    override fun perform(context: CastContext): SpellResult {
        val player = context.targetEntity as? Player?:return SpellResult.FAIL
        val task = PlayerLocationTrackTask.players[player.uniqueId]?:return SpellResult.FAIL
        var loc = task.getReverseTrack().getOrNull(recallTime-1)
        if (loc == null){
            loc = task.getReverseTrack().lastOrNull()
        }
        loc?:return SpellResult.CAST

        if (relative) {
            player.teleport(
                loc,
                TeleportFlag.Relative.X,
                TeleportFlag.Relative.Y,
                TeleportFlag.Relative.Z,
                TeleportFlag.Relative.YAW,
                TeleportFlag.Relative.PITCH
            )
        } else {
            player.teleport(loc)
        }

        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        recallTime = parameters.getInt("time", 100)
        relative = parameters.getBoolean("relative", false)
    }
}