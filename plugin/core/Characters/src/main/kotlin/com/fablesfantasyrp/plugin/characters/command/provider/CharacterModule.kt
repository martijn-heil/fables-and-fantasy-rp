/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.dal.model.CharacterData
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.OfflinePlayerProvider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.PlayerProvider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderProvider
import com.fablesfantasyrp.caturix.parametric.AbstractModule
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.parametric.provider.EnumProvider
import org.bukkit.Server
import org.bukkit.entity.Player

class CharacterModule(private val server: Server,
					  private val characters: CharacterRepository,
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
		bind(CharacterTrait::class.java).toProvider(EnumProvider(CharacterTrait::class.java))
	}
}
