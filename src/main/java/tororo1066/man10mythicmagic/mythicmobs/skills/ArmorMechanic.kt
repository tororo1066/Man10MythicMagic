package tororo1066.man10mythicmagic.mythicmobs.skills

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import org.bukkit.EntityEffect
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.Damageable


class ArmorMechanic(config: MythicLineConfig) : SkillMechanic(config.line, config), ITargetedEntitySkill {
    private val amount: Int
    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): Boolean {
        val bukkitTarget = BukkitAdapter.adapt(target)
        if (bukkitTarget !is Player)return false
        setDamage(Armor.HELMET,amount,bukkitTarget)
        setDamage(Armor.CHESTPLATE,amount,bukkitTarget)
        setDamage(Armor.LEGGINGS,amount,bukkitTarget)
        setDamage(Armor.BOOTS,amount,bukkitTarget)
        return true
    }

    init {
        this.isAsyncSafe = false
        setTargetsCreativePlayers(false)
        amount = config.getInteger(arrayOf("amount", "a"), 10)
    }

    private fun setDamage(armor : Armor, damage : Int, player : Player){
        val item = when(armor){
            Armor.HELMET -> player.inventory.helmet?:return
            Armor.CHESTPLATE -> player.inventory.chestplate?:return
            Armor.LEGGINGS -> player.inventory.leggings?:return
            Armor.BOOTS -> player.inventory.boots?:return
        }
        val meta = item.itemMeta as Damageable
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

    enum class Armor{
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }
}