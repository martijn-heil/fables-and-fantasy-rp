package com.fablesfantasyrp.plugin.characters.gui

import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.gui.element.Slider
import com.fablesfantasyrp.plugin.gui.ResultProducingInventoryGui
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.min

class CharacterStatsGui(plugin: JavaPlugin, minimums: CharacterStats, title: String = "Character stats",
						initial: CharacterStats = CharacterStats(0U, 0U, 0U, 0U)) :
		ResultProducingInventoryGui<CharacterStats>(plugin, title,
		arrayOf(
				"szzzzzzzz", // strength
				"dyyyyyyyy", // defense
				"axxxxxxxx", // agility
				"iwwwwwwww", // intelligence
				"         ",
				"    p    "
		)
) {
	private val absoluteMax = 12U
	private val strengthSlider 		= Slider('z', min(8U, absoluteMax - minimums.strength), initial.strength, { it + minimums.strength }, { canIncrease(it) })
	private val defenseSlider 		= Slider('y', min(8U, absoluteMax - minimums.defense), initial.defense, { it + minimums.defense }, { canIncrease(it) })
	private val agilitySlider 		= Slider('x', min(8U, absoluteMax - minimums.agility), initial.agility, { it + minimums.agility }, { canIncrease(it) })
	private val intelligenceSlider 	= Slider('w', min(8U, absoluteMax - minimums.intelligence), initial.intelligence, { it + minimums.intelligence }, { canIncrease(it) })

	private val statSliders = arrayOf(strengthSlider, defenseSlider, agilitySlider, intelligenceSlider)

	private val distributablePoints = 12U
	private val usedStatPoints get() =  statSliders.sumOf { it.value }
	private val freePoints get() = distributablePoints - usedStatPoints

	init {
		statSliders.forEach { addElement(it) }

		addElement(DynamicGuiElement('s') { _ ->
			StaticGuiElement('s', ItemStack(Material.IRON_SWORD),
					"$GOLD${BOLD}Strength: ${GREEN}${strengthSlider.value + minimums.strength}")
		})

		addElement(DynamicGuiElement('d') { _ ->
			StaticGuiElement('d', ItemStack(Material.SHIELD),
					"$GOLD${BOLD}Defense: ${GREEN}${defenseSlider.value + minimums.defense}")
		})

		addElement(DynamicGuiElement('a') { _ ->
			StaticGuiElement('a', ItemStack(Material.FEATHER),
					"$GOLD${BOLD}Agility: ${GREEN}${agilitySlider.value + minimums.agility}")
		})

		addElement(DynamicGuiElement('i') { _ ->
			StaticGuiElement('i', ItemStack(Material.BOOK),
					"$GOLD${BOLD}Intelligence: ${GREEN}${intelligenceSlider.value + minimums.intelligence}")
		})

		addElement(DynamicGuiElement('p') { _ ->
			if (freePoints > 0U) {
				StaticGuiElement('p', ItemStack(Material.AMETHYST_SHARD), freePoints.toInt(), { true }, "Points left: $freePoints")
			} else {
				StaticGuiElement('p', ItemStack(Material.WRITABLE_BOOK), freePoints.toInt(), {
					result.complete(CharacterStats(
							strength = strengthSlider.value,
							defense = defenseSlider.value,
							agility = agilitySlider.value,
							intelligence = intelligenceSlider.value
					))
					this.close(true)
					true
				},"$GREEN${BOLD}Submit")
			}
		})
	}

	private fun canIncrease(value: UInt): Boolean = freePoints > 0U
}
