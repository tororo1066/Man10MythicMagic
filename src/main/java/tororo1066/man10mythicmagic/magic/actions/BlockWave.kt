package tororo1066.man10mythicmagic.magic.actions

import com.elmakers.mine.bukkit.action.BaseSpellAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import tororo1066.man10mythicmagic.generator.BlockWaveEffect


class BlockWave : BaseSpellAction() {

    lateinit var material : Material
    private var rad = 1
    private var velocity = 1.0
    private var duration = 20

    override fun perform(context: CastContext?): SpellResult {
        if (context == null)return SpellResult.CANCELLED
        BlockWaveEffect(velocity,rad,4,duration,material,context.location!!)
        return SpellResult.CAST
    }

    override fun prepare(context: CastContext?, parameters: ConfigurationSection?) {
        if (context == null)return
        if (parameters == null)return
        material = Material.getMaterial(parameters.getString("material","STONE")!!)?:Material.STONE
        rad = parameters.getInt("radius",3)
        velocity = parameters.getDouble("velocity",1.0)
        duration = parameters.getInt("duration",20)
    }

    override fun finish(context: CastContext?) {

    }







}