package tororo1066.man10mythicmagic.listener

import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SMySQL
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sEvent.SEventInterface

class MythicMobDeathListener: SEventInterface<MythicMobDeathEvent>(Man10MythicMagic.plugin,MythicMobDeathEvent::class.java) {

    override fun executeEvent(e: MythicMobDeathEvent) {
        if (!Man10MythicMagic.logWorlds.contains(e.entity.world.name))return
        if (e.killer == null)return
        if (e.killer !is Player)return

        val hashMap = HashMap<String,Any>()
        hashMap["mobName"] = ChatColor.stripColor(e.mob.displayName?:"null")!!
        hashMap["mobIncludeName"] = e.mobType.internalName
        hashMap["killPlayer"] = e.killer.name
        hashMap["killPlayerUUID"] = e.killer.uniqueId
        val dropsString = SStr()
        e.drops.forEachIndexed { i, it ->
            if (i == 0){
                dropsString.append(it.itemMeta.displayName + "x" + it.amount)
            } else {
                dropsString.append(" " + it.itemMeta.displayName + "x" + it.amount)
            }

        }
        hashMap["drops"] = ChatColor.stripColor(dropsString.toString())!!
        hashMap["world"] = e.entity.world.name
        hashMap["spawner"] = e.mob.spawner?.internalName?:"null"
        hashMap["spawnLoc"] = toLocString(BukkitAdapter.adapt(e.mob.spawnLocation))
        hashMap["deadLoc"] = toLocString(e.entity.location)
        hashMap["deathTime"] = "now()"

        val insertQuery = SMySQL.insertQuery("mob_logger",hashMap)

        if (!SJavaPlugin.mysql.asyncExecute(insertQuery)){
            plugin.logger.warning("Failed save mythicMob data to mysql.")
        }

    }

    private fun toLocString(loc: Location): String {
        return "${loc.blockX} ${loc.blockY} ${loc.blockZ}"
    }
}