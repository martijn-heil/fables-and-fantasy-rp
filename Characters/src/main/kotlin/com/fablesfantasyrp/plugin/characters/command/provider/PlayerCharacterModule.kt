package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.PlayerCharacter
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.OfflinePlayerProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.PlayerProvider
import com.sk89q.intake.parametric.AbstractModule
import org.bukkit.Server
import org.bukkit.entity.Player

class PlayerCharacterModule(private val server: Server) : AbstractModule() {
	override fun configure() {
		bind(PlayerCharacter::class.java).toProvider(PlayerCharacterProvider(server))
		bind(Player::class.java).annotatedWith(AllowCharacterName::class.java)
				.toProvider(AllowCharacterNamePlayerProvider(server,
						PlayerProvider(server, OfflinePlayerProvider(server))))
	}
}
