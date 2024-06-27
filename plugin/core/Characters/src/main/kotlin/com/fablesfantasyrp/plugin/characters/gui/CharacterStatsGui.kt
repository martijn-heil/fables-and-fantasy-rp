/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.characters.gui

import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.gui.ResultProducingInventoryGui
import com.fablesfantasyrp.plugin.gui.element.Slider
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.min

class CharacterStatsGui(plugin: JavaPlugin,
						minimums: CharacterStats,
						traits: Set<CharacterTrait>,
						title: String = "Character stats",
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

	private val distributablePoints = if (traits.contains(CharacterTrait.PAINFULLY_AVERAGE)) 14U else 12U
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
