package tororo1066.man10mythicmagic.mythicmobs

import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.mysql.ultimate.USQLTable
import tororo1066.tororopluginapi.mysql.ultimate.USQLVariable

class MobDeathLoggerTable: USQLTable("mob_logger", SJavaPlugin.mysql) {

    companion object{
        val id = USQLVariable(USQLVariable.Int,autoIncrement = true,index = USQLVariable.Index.PRIMARY)
        val mobName = USQLVariable(USQLVariable.Text)
        val mobIncludeName = USQLVariable(USQLVariable.Text)
        val killPlayer = USQLVariable(USQLVariable.VarChar,16)
        val killPlayerUUID = USQLVariable(USQLVariable.VarChar,36)
        val drops = USQLVariable(USQLVariable.Text)
        val world = USQLVariable(USQLVariable.Text)
        val spawner = USQLVariable(USQLVariable.Text, nullable = true)
        val spawnLoc = USQLVariable(USQLVariable.Text)
        val deadLoc = USQLVariable(USQLVariable.Text)
        val deathTime = USQLVariable(USQLVariable.DateTime)
    }
}