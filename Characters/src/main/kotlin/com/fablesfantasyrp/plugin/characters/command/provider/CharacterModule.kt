package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.playerInstanceManager
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.OfflinePlayerProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.PlayerProvider
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.provider.EnumProvider
import org.bukkit.Server
import org.bukkit.entity.Player

class CharacterModule(private val server: Server,
						private val characterRepository: EntityCharacterRepository,
						private val playerInstanceProvider: Provider<PlayerInstance>) : AbstractModule() {
	override fun configure() {
		bind(Character::class.java).toProvider(CharacterProvider(server))
		bind(CharacterStatKind::class.java).toProvider(EnumProvider(CharacterStatKind::class.java))
		bind(CharacterData::class.java).toProvider(CharacterProvider(server) as Provider<CharacterData>)
		bind(Player::class.java).annotatedWith(AllowCharacterName::class.java)
				.toProvider(AllowCharacterNamePlayerProvider(server,
						PlayerProvider(server, OfflinePlayerProvider(server))))
		bind(PlayerInstance::class.java).annotatedWith(AllowCharacterName::class.java)
				.toProvider(AllowCharacterNamePlayerInstanceProvider(server,
						characterRepository,
						playerInstanceProvider,
						playerInstanceManager))
	}
}
