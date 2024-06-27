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
package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.MageAbilities
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderProvider
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import org.bukkit.entity.Player

class MageAbilityProvider(private val profileManager: ProfileManager,
						  private val characters: CharacterRepository,
						  private val mages: MageRepository) : Provider<MageAbility> {
	override val isProvided: Boolean = false

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): MageAbility {
		val ability = MageAbilities.forId(arguments.next()) ?: throw ArgumentParseException("Ability not found.")
		if (modifiers.find { it is OwnAbility } != null) {
			val player = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)
			val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) }
				?: throw ArgumentParseException("You are not in character.")
			val mage = mages.forCharacter(character) ?: throw ArgumentParseException("You are not a mage.")
			if (mage.magicPath != ability.magicPath && mage.magicPath.basePath != ability.magicPath)
				throw ArgumentParseException("You don't have access to this ability.")
		}
		return ability
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		if (modifiers.find { it is OwnAbility } != null) {
			val player = locals.get("sender") as? Player ?: return emptyList()
			val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) }
					?: return emptyList()
			val mage = mages.forCharacter(character) ?: return emptyList()
			return MageAbilities.forPath(mage.magicPath).asSequence()
					.map { it.id }
				.filter { it.startsWith(prefix) }
					.toList()
		} else {
			return MageAbilities.all.asSequence()
					.map { it.id }
					.filter { it.startsWith(prefix) }
					.toList()
		}
	}
}
