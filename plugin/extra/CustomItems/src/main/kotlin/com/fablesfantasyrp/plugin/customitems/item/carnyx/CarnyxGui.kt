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
package com.fablesfantasyrp.plugin.customitems.item.carnyx

import com.fablesfantasyrp.plugin.gui.Icon
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.plugin.java.JavaPlugin

class CarnyxGui(plugin: JavaPlugin) : InventoryGui(plugin, "Carnyx", arrayOf(
	"   123   ",
	"   4 5   ",
	"   678   ")) {
	init {
		this.addElement(playButton(1, "high"))
		this.addElement(playButton(2, "spook"))
		this.addElement(playButton(3, "high_alt"))

		this.addElement(playButton(4, "regular"))
		this.addElement(playButton(5, "call"))

		this.addElement(playButton(6, "low"))
		this.addElement(playButton(7, "bass"))
		this.addElement(playButton(8, "low_alt"))
	}

	private fun playButton(digit: Int, sound: String)
		= StaticGuiElement(digit.digitToChar(), Icon.digit(digit), {
			val world = it.whoClicked.location.world
			val range = 768
			val volume = (range / 16).toFloat()
			world.playSound(Sound.sound(
				Key.key("fablesfantasyrp", "ambient.custom.carnyx.${sound}"),
				Sound.Source.AMBIENT, volume, 1f))
				true
		   }, sound)
}
