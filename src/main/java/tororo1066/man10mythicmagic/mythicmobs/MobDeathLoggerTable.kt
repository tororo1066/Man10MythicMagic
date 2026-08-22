package tororo1066.man10mythicmagic.mythicmobs

import tororo1066.man10mythicmagic.Man10MythicMagic
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.database.SDatabase

class MobDeathLoggerTable {

    private val sDatabase = SDatabase.newInstance(Man10MythicMagic.plugin, null, "mysql")

    init {
        sDatabase.backGroundCreateTable(
            "mob_logger",
            mapOf(
                "id" to SDBVariable(SDBVariable.Int, autoIncrement = true, index = SDBVariable.Index.PRIMARY),
                "mobName" to SDBVariable(SDBVariable.Text),
                "mobIncludeName" to SDBVariable(SDBVariable.Text),
                "killPlayer" to SDBVariable(SDBVariable.VarChar, 16),
                "killPlayerUUID" to SDBVariable(SDBVariable.VarChar, 36),
                "drops" to SDBVariable(SDBVariable.Text),
                "world" to SDBVariable(SDBVariable.Text),
                "spawner" to SDBVariable(SDBVariable.Text, nullable = true),
                "spawnLoc" to SDBVariable(SDBVariable.Text),
                "deadLoc" to SDBVariable(SDBVariable.Text),
                "deathTime" to SDBVariable(SDBVariable.DateTime)
            )
        )
    }

    fun insert(data: Map<String, Any?>) {
        sDatabase.backGroundInsert("mob_logger", data) { result ->
            if (!result) {
                Man10MythicMagic.plugin.logger.warning("Failed to insert mob_logger")
            }
        }
    }

    fun close() {
        sDatabase.close()
    }
}