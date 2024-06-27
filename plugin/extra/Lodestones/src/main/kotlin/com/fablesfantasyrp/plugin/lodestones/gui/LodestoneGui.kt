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
package com.fablesfantasyrp.plugin.lodestones.gui

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.shortName
import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.gui.Icon
import com.fablesfantasyrp.plugin.lodestones.MAX_LODESTONE_SLOTS
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.flaunch
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.itemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.meta
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.min

class LodestoneGui(private val plugin: JavaPlugin,
				   private val player: Player,
				   private val character: Character,
				   private val slotCount: Int,
				   private val clickedLodestone: Lodestone,
				   private val characterLodestoneRepository: CharacterLodestoneRepository)
	: InventoryGui(plugin, player, "${character.shortName}'s lodestones", arrayOf("gggggggxd")) {
	private val server = plugin.server

	init {
		this.addElement(DynamicGuiElement('g') { viewer ->
			val lodestones = characterLodestoneRepository.forCharacter(character).sortedBy { it.name }
			val group = GuiElementGroup('g')

			group.addElements(lodestones.map { lodestone ->
				StaticGuiElement('g', getItem(lodestone), {
					characterLodestoneRepository.remove(character, lodestone)
					characterLodestoneRepository.add(character, clickedLodestone)
					draw()
					true
				}, "${ChatColor.GOLD}${lodestone.name}", "${ChatColor.GRAY}Click to replace this lodestone.")
			})

			group.addElements((min(lodestones.size, slotCount) until slotCount).map {
				StaticGuiElement('g', ItemStack(Material.SKELETON_SKULL), {
					characterLodestoneRepository.add(character, clickedLodestone)
					draw()
					true
				},
					"${ChatColor.DARK_AQUA}Click to add!",
					"${ChatColor.GRAY}Clicking this will allow you to add",
					"${ChatColor.GRAY}this lodestone to your collection.")
			})

			group.addElements((slotCount until MAX_LODESTONE_SLOTS).map {
				StaticGuiElement('g', ItemStack(Material.WITHER_SKELETON_SKULL), { true },
					"${ChatColor.RED}Locked slot!",
					"${ChatColor.GRAY}Higher VIP ranks may access this slot.")
			})

			group
		})

		this.addElement(StaticGuiElement('d', Icon.TRASH_BIN, {
			unlinkLodestone()
			true
		}, "${ChatColor.RED}Delete a lodestone",
			"${ChatColor.GRAY}Click here to select a lodestone to unlink.", ))
	}

	private fun unlinkLodestone() {
		flaunch {
			val lodestone = GuiSingleChoice<Lodestone>(plugin, "Please select a lodestone to unlink",
				characterLodestoneRepository.forCharacter(character).asSequence(),
				{ getItem(it) },
				{ "${ChatColor.GOLD}${it.name}\n${ChatColor.GRAY}Click to unlink this lodestone." }).execute(player)

			characterLodestoneRepository.remove(character, lodestone)
			draw()
		}
	}

	private fun getItem(lodestone: Lodestone): ItemStack
		= itemStack(Material.LODESTONE) {
			meta {
				displayName(Component.text(lodestone.name).color(NamedTextColor.GOLD))
			}
		}
}
