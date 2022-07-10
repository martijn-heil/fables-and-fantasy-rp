package com.fablesfantasyrp.plugin.chat.gui

import com.fablesfantasyrp.plugin.chat.chat
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class ChatColorGui(plugin: JavaPlugin) : InventoryGui(plugin, "Please select a chat color",
		arrayOf(
			"abcdefghi",
			"jkl")
) {
	init {
		val letters = LinkedList(("abcdefghijkl").toList())
		listOf(
				Pair(NamedTextColor.DARK_BLUE, Material.BLUE_WOOL),
				Pair(NamedTextColor.DARK_GREEN, Material.GREEN_WOOL),
				Pair(NamedTextColor.DARK_AQUA, Material.CYAN_WOOL),
				Pair(NamedTextColor.DARK_RED, Material.RED_WOOL),
				Pair(NamedTextColor.DARK_PURPLE, Material.PURPLE_WOOL),
				Pair(NamedTextColor.GOLD, Material.ORANGE_WOOL),
				Pair(NamedTextColor.BLUE, Material.BLUE_WOOL),
				Pair(NamedTextColor.GREEN, Material.LIME_WOOL),
				Pair(NamedTextColor.AQUA, Material.LIGHT_BLUE_WOOL),
				Pair(NamedTextColor.RED, Material.PINK_TERRACOTTA),
				Pair(NamedTextColor.LIGHT_PURPLE, Material.PINK_WOOL),
				Pair(NamedTextColor.YELLOW, Material.YELLOW_WOOL),
		).forEach { this.addElement(ChatColorSelectorElement(it.first, letters.pop(), ItemStack(it.second))) }
	}

	private class ChatColorSelectorElement(private val color: TextColor, slot: Char, item: ItemStack) :
			StaticGuiElement(slot, item, {
				if (it.whoClicked is Player) {
					val player = it.whoClicked as Player
					val chatPlayerEntity = player.chat
					chatPlayerEntity.chatStyle = chatPlayerEntity.chatStyle?.color(color) ?: Style.style(color)
				}
				it.gui.close()
				true
			}, LegacyComponentSerializer.legacySection().serialize(Component.text(color.toString()).color(color)))
}
