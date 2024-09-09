package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.builtin.EntityProjectileAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.block.MaterialAndData
import com.elmakers.mine.bukkit.api.item.ItemData
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.elmakers.mine.bukkit.utility.CompatibilityLib
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.TextDisplay

class DisplayProjectile: EntityProjectileAction() {

    enum class Type(val type: EntityType, val clazz: Class<out Display>) {
        ITEM(EntityType.ITEM_DISPLAY, ItemDisplay::class.java),
        BLOCK(EntityType.BLOCK_DISPLAY, BlockDisplay::class.java),
        TEXT(EntityType.TEXT_DISPLAY, TextDisplay::class.java)
    }

    var billboard: Display.Billboard = Display.Billboard.FIXED


    var type = Type.ITEM
    var item: ItemData? = null
    var text: Component? = null

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        type = Type.valueOf(parameters.getString("type", "ITEM")!!.uppercase())
        when(type){
            Type.ITEM -> item = context.controller.getOrCreateItem(parameters.getString("item", "minecraft:stone")!!)
            Type.TEXT -> text = MiniMessage.miniMessage().deserialize(parameters.getString("text", "Undefined Message")!!)
            else -> {}
        }
    }

    override fun start(context: CastContext): SpellResult {
        val location = adjustStartLocation(sourceLocation.getLocation(context))
        val entity = setEntity(context.controller, CompatibilityLib.getCompatibilityUtils().createEntity(location, type.type)) as Display
        entity.billboard = billboard
        when(type) {
            Type.ITEM -> {
                entity as ItemDisplay
                entity.itemStack = item?.itemStack
            }
            Type.BLOCK -> {
                entity as BlockDisplay
                val brush = context.brush ?: return SpellResult.FAIL
                entity.block = Bukkit.getUnsafe().fromLegacy(brush.material, brush.blockData?:0)
            }
            Type.TEXT -> {
                entity as TextDisplay
                entity.text(text)
            }
        }

        return SpellResult.CAST
    }
}