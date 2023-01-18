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
