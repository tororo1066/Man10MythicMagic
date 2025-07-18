package tororo1066.man10mythicmagic.noused

import com.elmakers.mine.bukkit.action.ActionFactory
import org.bukkit.Bukkit
import tororo1066.man10mythicmagic.Man10MythicMagic

class NoUsed {

    companion object {
        fun register(){
            if (!Man10MythicMagic.plugin.config.getBoolean("useNoUsedModule"))
                return
            if (Bukkit.getPluginManager().getPlugin("Magic") != null){
                MovementListener()
                ActionFactory.registerActionClass("MovingPush", MovingPush::class.java)
            }
        }
    }
}