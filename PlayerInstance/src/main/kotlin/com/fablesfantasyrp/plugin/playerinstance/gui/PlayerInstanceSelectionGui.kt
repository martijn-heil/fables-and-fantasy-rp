package com.fablesfantasyrp.plugin.playerinstance.gui

import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin


class PlayerInstanceSelectionGui(plugin: JavaPlugin, values: Sequence<PlayerInstance>) : GuiSingleChoice<PlayerInstance>(
		plugin, "Choose your character",
		values,
		getItemStack = { ItemStack(Material.PLAYER_HEAD) },
		getText = { ChatColor.GOLD.toString() + "#${it.id}\n${it.description}" }) {
}
