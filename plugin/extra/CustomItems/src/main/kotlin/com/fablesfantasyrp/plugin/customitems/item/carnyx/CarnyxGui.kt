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
