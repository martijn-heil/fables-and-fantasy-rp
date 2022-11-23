package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.OfflinePlayerProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.PlayerProvider
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.provider.EnumProvider
import org.bukkit.Server
import org.bukkit.entity.Player

class CharacterModule(private val server: Server) : AbstractModule() {
	override fun configure() {
		bind(CharacterStatKind::class.java).toProvider(EnumProvider(CharacterStatKind::class.java))
		bind(CharacterData::class.java).toProvider(CharacterProvider(server))
		bind(Player::class.java).annotatedWith(AllowCharacterName::class.java)
				.toProvider(AllowCharacterNamePlayerProvider(server,
						PlayerProvider(server, OfflinePlayerProvider(server))))
	}
}
