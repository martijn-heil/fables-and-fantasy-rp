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
package com.fablesfantasyrp.plugin.gui

import de.themoep.inventorygui.StaticGuiElement
import kotlinx.coroutines.CancellationException
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class GuiConfirm(plugin: JavaPlugin,
						 title: String,
						 confirmText: String = "${ChatColor.GREEN}Confirm",
						 cancelText: String = "${ChatColor.RED}Cancel"
)
	: ResultProducingInventoryGui<Boolean>(plugin, title,
	arrayOf("gggagbggg")) {
		init {
			this.addElement(StaticGuiElement('a', Icon.CHECKMARK, {
				result.complete(true)
				true
			}, confirmText))

			this.addElement(StaticGuiElement('b', Icon.X, {
				result.complete(false)
				true
			}, cancelText))
		}
}

suspend fun Player.confirm(question: String): Boolean {
	return try {
		GuiConfirm(PLUGIN, question).execute(this)
	} catch (e: CancellationException) {
		false
	}
}
