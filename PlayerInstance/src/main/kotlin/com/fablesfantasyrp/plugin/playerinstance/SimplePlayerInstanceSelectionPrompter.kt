package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.gui.PlayerInstanceSelectionGui
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class SimplePlayerInstanceSelectionPrompter(private val plugin: JavaPlugin) : PlayerInstanceSelectionPrompter {
	private val server = plugin.server

	override suspend fun promptSelect(player: Player, playerInstances: Collection<PlayerInstance>): PlayerInstance {
		return player.promptGui(PlayerInstanceSelectionGui(plugin, playerInstances.asSequence()))
	}
}
