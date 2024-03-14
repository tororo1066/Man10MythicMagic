package tororo1066.man10mythicmagic.mythicmobs

import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.mysql.ultimate.USQLTable

class MobDeathLoggerTable: USQLTable("mob_logger", SJavaPlugin.mysql) {

    companion object{
        val id = SDBVariable(SDBVariable.Int,autoIncrement = true,index = SDBVariable.Index.PRIMARY)
        val mobName = SDBVariable(SDBVariable.Text)
        val mobIncludeName = SDBVariable(SDBVariable.Text)
        val killPlayer = SDBVariable(SDBVariable.VarChar,16)
        val killPlayerUUID = SDBVariable(SDBVariable.VarChar,36)
        val drops = SDBVariable(SDBVariable.Text)
        val world = SDBVariable(SDBVariable.Text)
        val spawner = SDBVariable(SDBVariable.Text, nullable = true)
        val spawnLoc = SDBVariable(SDBVariable.Text)
        val deadLoc = SDBVariable(SDBVariable.Text)
        val deathTime = SDBVariable(SDBVariable.DateTime)
    }
}