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
package com.fablesfantasyrp.plugin.profile.gui

import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin


class ProfileSelectionGui(plugin: JavaPlugin, values: Sequence<Profile>) : GuiSingleChoice<Profile>(
		plugin, "Choose your profile",
		values,
		getItemStack = { ItemStack(Material.PLAYER_HEAD) },
		getText = { ChatColor.GOLD.toString() + "#${it.id}\n${it.description}" }) {
	override suspend fun execute(who: HumanEntity): Profile {
		this.show(who)
		val result = result.await()
		this.close(true)
		return result
	}
}
