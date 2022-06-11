package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.gui.GuiSlider
import com.fablesfantasyrp.plugin.gui.ResultProducingGui
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class CharacterStatsGui(plugin: JavaPlugin, minimums: CharacterStats, title: String = "Character stats") :
		ResultProducingGui<CharacterStats>(plugin, title,
		arrayOf(
				"szzzzzzzz", // strength
				"dyyyyyyyy", // defense
				"axxxxxxxx", // agility
				"iwwwwwwww", // intelligence
				"         ",
				"    p    "
		)
) {
	private val strengthSlider 		= GuiSlider('z', 8U, 0U, { it + minimums.strength }, { canIncrease(it) })
	private val defenseSlider 		= GuiSlider('y', 8U, 0U, { it + minimums.defense }, { canIncrease(it) })
	private val agilitySlider 		= GuiSlider('x', 8U, 0U, { it + minimums.agility }, { canIncrease(it) })
	private val intelligenceSlider 	= GuiSlider('w', 8U, 0U, { it + minimums.intelligence }, { canIncrease(it) })

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
				StaticGuiElement('p', ItemStack(Material.AMETHYST_SHARD), "Points left: $freePoints")
			} else {
				StaticGuiElement('p', ItemStack(Material.WRITABLE_BOOK), {
					result.complete(CharacterStats(
							strength = strengthSlider.value + minimums.strength,
							defense = defenseSlider.value + minimums.defense,
							agility = agilitySlider.value + minimums.agility,
							intelligence = intelligenceSlider.value + minimums.intelligence
					))
					this.close(true)
					true
				},"$GREEN${BOLD}Submit")
			}
		})
	}

	private fun canIncrease(value: UInt): Boolean = freePoints > 0U
}
