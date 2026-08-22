package tororo1066.man10mythicmagic.hud

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.ShadowColor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.customhudapi.hud.element.AbstractHud
import tororo1066.customhudapi.hud.layout.HudRenderContext
import tororo1066.customhudapi.hud.render.HudCanvas
import tororo1066.man10mythicmagic.magic.effect.CustomPotionEffectInstance
import tororo1066.man10mythicmagic.magic.effect.CustomPotionManager

/**
 * A player-scoped CustomHud which renders the active custom potion effects.
 *
 * The resource pack supplies a font for every row. Man10MythicMagic supplies
 * only the active effects and their configured icon code points.
 */
class CustomPotionEffectRenderer(
    private val player: Player,
    private val maxEffects: Int,
    private val rowStartX: Int,
    private val rowFontPattern: String,
    private val iconAdvance: Int,
    private val defaultIcon: String?,
    priority: Int
) : AbstractHud("custom-potion-effects", priority) {

    private var renderedIcons = emptyList<String>()

    init {
        isVisible = false
    }

    override fun tick(context: HudRenderContext) {
        val nextIcons = activeEffects().mapNotNull(::iconFor)
        if (nextIcons != renderedIcons) {
            renderedIcons = nextIcons
            isVisible = nextIcons.isNotEmpty()
            invalidate()
        }
    }

    override fun render(context: HudRenderContext, canvas: HudCanvas) {
        canvas.begin()
        renderedIcons.forEachIndexed { index, icon ->
            canvas.moveX(rowStartX)
            canvas.component(Component.text(icon).font(rowFont(index)).shadowColor(ShadowColor.shadowColor(0,0,0,0)), iconAdvance)
            canvas.moveX(-rowStartX - iconAdvance)
        }
        canvas.end()
    }

    private fun activeEffects(): List<CustomPotionEffectInstance> =
        CustomPotionManager.customPotionEffectInstances[player.uniqueId]
            ?.values
            ?.mapNotNull { instances ->
                instances.asSequence()
                    .filterNot { it.shouldRemove }
                    .maxByOrNull { it.amplifier }
            }
            ?.filter { it.effect.displayEnabled }
            ?.sortedWith(
                compareByDescending<CustomPotionEffectInstance> { it.effect.displayPriority }
                    .thenBy { it.effect.name }
            )
            ?.take(maxEffects.coerceAtLeast(0))
            .orEmpty()

    private fun iconFor(effect: CustomPotionEffectInstance): String? {
        val frames = effect.effect.displayIconFrames
            .mapNotNull(::codePoint)
        if (frames.isNotEmpty()) {
            val remaining = (effect.duration - effect.currentTick).coerceAtLeast(0)
            return if (frames.size > 1 &&
                effect.effect.displayPulseBeforeTicks > 0 &&
                remaining <= effect.effect.displayPulseBeforeTicks
            ) {
                pulsingFrame(frames, effect.currentTick, effect.effect.displayPulseIntervalTicks)
            } else {
                frames.first()
            }
        }

        return effect.effect.displayIcon
            ?.takeIf { it.isNotBlank() }
            ?.let(::codePoint)
            ?: defaultIcon
    }

    private fun pulsingFrame(frames: List<String>, currentTick: Int, interval: Int): String {
        val lastIndex = frames.lastIndex
        val phase = (currentTick / interval) % (lastIndex * 2)
        val index = if (phase <= lastIndex) phase else lastIndex * 2 - phase
        return frames[index]
    }

    private fun rowFont(index: Int): Key = Key.key(rowFontPattern.replace("%d", index.toString()))

    override fun toString(): String = """
        CustomPotionEffectRenderer(
            player=${player.name},
            maxEffects=$maxEffects,
            rowStartX=$rowStartX,
            rowFontPattern=$rowFontPattern,
            iconAdvance=$iconAdvance,
            defaultIcon=$defaultIcon,
            priority=$priority
        )
    """.trimIndent()

    companion object {
        fun fromConfig(player: Player, config: ConfigurationSection): CustomPotionEffectRenderer {
            val rowFontPattern = requireNotNull(config.getString("row-font-pattern")?.takeIf { it.isNotBlank() }) {
                "custom-potion-effect-hud.row-font-pattern is required"
            }
            require("%d" in rowFontPattern) {
                "custom-potion-effect-hud.row-font-pattern must contain %d"
            }

            return CustomPotionEffectRenderer(
                player = player,
                maxEffects = config.getInt("max-effects", 8),
                rowStartX = config.getInt("row-start-x", 0),
                rowFontPattern = rowFontPattern,
                iconAdvance = config.getInt("icon-advance", 0),
                defaultIcon = config.getString("default-icon")
                    ?.takeIf { it.isNotBlank() }
                    ?.let(::codePoint),
                priority = config.getInt("priority", 0)
            )
        }

        private fun codePoint(value: String): String? = runCatching {
            val codePoint = value.removePrefix("U+").toInt(16)
            String(Character.toChars(codePoint))
        }.getOrNull()
    }
}
