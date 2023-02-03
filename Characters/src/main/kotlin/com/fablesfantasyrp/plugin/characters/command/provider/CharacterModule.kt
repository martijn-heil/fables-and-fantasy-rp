package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileProvider
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.Services
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.OfflinePlayerProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.PlayerProvider
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.provider.EnumProvider
import org.bukkit.Server
import org.bukkit.entity.Player

class CharacterModule(private val server: Server = Services.get(),
					  private val characters: EntityCharacterRepository = Services.get(),
					  private val profiles: EntityProfileRepository = Services.get(),
					  private val profileManager: ProfileManager = Services.get(),
					  private val profileProvider: Provider<Profile> = ProfileProvider(profiles, profileManager)) : AbstractModule() {
	override fun configure() {
		bind(Character::class.java).toProvider(CharacterProvider(server, characters))
		bind(CharacterData::class.java).toProvider(CharacterProvider(server, characters) as Provider<CharacterData>)
		bind(CharacterStatKind::class.java).toProvider(EnumProvider(CharacterStatKind::class.java))
		bind(Race::class.java).toProvider(EnumProvider(Race::class.java))
		bind(Player::class.java).annotatedWith(AllowCharacterName::class.java).toProvider(AllowCharacterNamePlayerProvider(server, PlayerProvider(server, OfflinePlayerProvider(server)), profileManager, characters))
		bind(Profile::class.java).annotatedWith(AllowCharacterName::class.java).toProvider(AllowCharacterNameProfileProvider(server, characters, profileProvider, profileManager))
	}
}
