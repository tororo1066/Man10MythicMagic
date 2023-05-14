package tororo1066.man10mythicmagic.tasks

import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.utils.toPlayer
import java.util.*

class PlayerLocationTrackTask(private val uuid: UUID): BukkitRunnable() {

    val track = ArrayList<Location>()

    companion object{
        val players = HashMap<UUID, PlayerLocationTrackTask>()
        var trackLimit = 400
    }

    init {
        runTaskTimer(Man10MythicMagic.plugin,0,1)
    }

    fun getReverseTrack(): List<Location> {
        return track.reversed()
    }

    override fun run() {
        val p = uuid.toPlayer()
        if (p == null){
            cancel()
            players.remove(uuid)
            return
        }
        track.add(p.location)
        if (track.size > trackLimit){
            track.removeFirstOrNull()
        }
    }
}