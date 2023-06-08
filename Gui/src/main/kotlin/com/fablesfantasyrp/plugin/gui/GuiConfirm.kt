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
