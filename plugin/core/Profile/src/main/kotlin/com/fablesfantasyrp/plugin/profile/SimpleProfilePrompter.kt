package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.gui.ProfileSelectionGui
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class SimpleProfilePrompter(private val plugin: JavaPlugin) : ProfilePrompter {
	override suspend fun promptSelect(player: Player, profiles: Collection<Profile>): Profile {
		val gui = ProfileSelectionGui(plugin, profiles.asSequence())
		return player.promptGui(gui)
	}
}
