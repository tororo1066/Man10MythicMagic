package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.core.skills.SkillMechanic
import org.bukkit.EntityEffect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import tororo1066.man10mythicmagic.Man10MythicMagic
import java.io.File


class ArmorMechanic(config: MythicLineConfig, file: File?) : SkillMechanic(Man10MythicMagic.mythicMobs.skillManager,file,config.line,config), ITargetedEntitySkill {
    private val amount: Int
    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): SkillResult {
        val bukkitTarget = BukkitAdapter.adapt(target)
        if (bukkitTarget !is Player)return SkillResult.CONDITION_FAILED
        setDamage(Armor.HELMET,amount,bukkitTarget)
        setDamage(Armor.CHESTPLATE,amount,bukkitTarget)
        setDamage(Armor.LEGGINGS,amount,bukkitTarget)
        setDamage(Armor.BOOTS,amount,bukkitTarget)
        return SkillResult.SUCCESS
    }

    init {
        this.setTargetsCreativePlayers(false)
        this.isAsyncSafe = false
        amount = config.getInteger(arrayOf("amount", "a"), 10)
    }

    private fun setDamage(armor : Armor, damage : Int, player : Player){
        val item = when(armor){
            Armor.HELMET -> player.inventory.helmet?:return
            Armor.CHESTPLATE -> player.inventory.chestplate?:return
            Armor.LEGGINGS -> player.inventory.leggings?:return
            Armor.BOOTS -> player.inventory.boots?:return
        }
        if (!armorCheck(item))return
        val meta = item.itemMeta as Damageable
        if (meta.isUnbreakable)return
        meta.damage += damage
        item.itemMeta = meta
        if ((item.itemMeta as Damageable).damage >= item.type.maxDurability){
            when(armor){
                Armor.HELMET ->player.inventory.helmet = null
                Armor.CHESTPLATE -> player.inventory.chestplate = null
                Armor.LEGGINGS -> player.inventory.leggings = null
                Armor.BOOTS -> player.inventory.boots = null
            }
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK,1f,1f)
            player.playEffect(EntityEffect.BREAK_EQUIPMENT_HELMET)
        }
    }

    fun armorCheck(item: ItemStack): Boolean {
        val type = item.type
        return (type == Material.LEATHER_HELMET || type == Material.LEATHER_CHESTPLATE ||
                type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_BOOTS ||
                type == Material.CHAINMAIL_HELMET || type == Material.CHAINMAIL_CHESTPLATE ||
                type == Material.CHAINMAIL_LEGGINGS || type == Material.CHAINMAIL_BOOTS ||
                type == Material.IRON_HELMET || type == Material.IRON_CHESTPLATE ||
                type == Material.IRON_LEGGINGS || type == Material.IRON_BOOTS ||
                type == Material.DIAMOND_HELMET || type == Material.DIAMOND_CHESTPLATE ||
                type == Material.DIAMOND_LEGGINGS || type == Material.DIAMOND_BOOTS ||
                type == Material.GOLDEN_HELMET || type == Material.GOLDEN_CHESTPLATE ||
                type == Material.GOLDEN_LEGGINGS || type == Material.GOLDEN_BOOTS ||
                type == Material.NETHERITE_HELMET || type == Material.NETHERITE_CHESTPLATE ||
                type == Material.NETHERITE_LEGGINGS || type == Material.NETHERITE_BOOTS)
    }

    enum class Armor{
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }
}