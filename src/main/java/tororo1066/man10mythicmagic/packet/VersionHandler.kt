package tororo1066.man10mythicmagic.packet

import org.bukkit.Bukkit
import java.util.IdentityHashMap

object VersionHandler {

    val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".", "_")

    val cache = IdentityHashMap<Class<*>, Any>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getInstance(clazz: Class<T>): T {
        cache[clazz]?.let {
            return it as T
        }
        var instanceClass: Class<*>? = null
        val instanceClassName = clazz.simpleName.replaceFirst("I", "")
        try {
            instanceClass = Class.forName("tororo1066.man10mythicmagic.packet.$version.$instanceClassName")
        } catch (e: ClassNotFoundException) {
            try {
                instanceClass = Class.forName("tororo1066.man10mythicmagic.packet.latest.$instanceClassName")
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        val instance = instanceClass?.getConstructor()?.newInstance() ?: throw ClassNotFoundException("Cannot find class for $instanceClassName")

        cache[clazz] = instance

        return instance as T
    }
}