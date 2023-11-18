package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.dal.model.CharacterData
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.OfflinePlayerProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.PlayerProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.provider.EnumProvider
import org.bukkit.Server
import org.bukkit.entity.Player

class CharacterModule(private val server: Server,
					  private val characters: CharacterRepository,
					  private val traits: CharacterTraitRepository,
					  private val profileManager: ProfileManager,
					  private val profileProvider: Provider<Profile>) : AbstractModule() {
	override fun configure() {
		bind(Character::class.java).toProvider(CharacterProvider(server, characters, profileManager))
		bind(CharacterData::class.java).toProvider(CharacterProvider(server, characters, profileManager) as Provider<CharacterData>)
		bind(CharacterStatKind::class.java).toProvider(EnumProvider(CharacterStatKind::class.java))
		bind(Race::class.java).toProvider(EnumProvider(Race::class.java))
		bind(Player::class.java).annotatedWith(AllowCharacterName::class.java).toProvider(AllowCharacterNamePlayerProvider(server, PlayerProvider(server, OfflinePlayerProvider(server)), profileManager, characters))
		bind(Profile::class.java).annotatedWith(AllowCharacterName::class.java).toProvider(AllowCharacterNameProfileProvider(server, characters, profileProvider, profileManager))
		bind(Character::class.java).annotatedWith(Sender::class.java).toProvider(CharacterSenderProvider(profileManager, characters, BukkitSenderProvider(Player::class.java)))
		bind(CharacterTrait::class.java).toProvider(CharacterTraitProvider(traits))
	}
}
