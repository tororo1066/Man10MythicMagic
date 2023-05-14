package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.CompoundAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.tasks.PlayerLocationTrackTask

class RecallBackFuture: CompoundAction() {
    private var recallTime = 100

    override fun perform(context: CastContext): SpellResult {
        val player = context.targetEntity as? Player?:return SpellResult.FAIL
        val task = PlayerLocationTrackTask.players[player.uniqueId]?:return SpellResult.FAIL
        val loc = task.getReverseTrack().getOrNull(recallTime-1)?:return SpellResult.CAST

        player.teleport(loc)

        return SpellResult.CAST
    }

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        recallTime = parameters.getInt("time", 100)
    }
}