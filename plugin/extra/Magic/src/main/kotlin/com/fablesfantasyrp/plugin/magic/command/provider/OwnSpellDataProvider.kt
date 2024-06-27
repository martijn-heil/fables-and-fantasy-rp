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

import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderProvider
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.authorizer.SpellAuthorizer
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.entity.Player

class OwnSpellDataProvider(private val spellProvider: Provider<SpellData>,
						   private val profileManager: ProfileManager,
						   private val characters: CharacterRepository,
						   private val spellAuthorizer: SpellAuthorizer) : Provider<SpellData> {

	override val isProvided: Boolean = false

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): SpellData {
		val sender = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)
		val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
				?: throw ArgumentParseException("You are not currently in character.")
		val spell = spellProvider.get(arguments, modifiers)
		if (!spellAuthorizer.hasSpell(character, spell)) throw ArgumentParseException("You don't have access to this spell.")
		return spell
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		val sender = locals.get("sender") as? Player ?: return emptyList()
		val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
				?: return emptyList()

		return spellAuthorizer.getSpells(character)
			.asSequence()
			.filter { it.id.startsWith(prefix) }
			.map { it.id }
			.toList()
	}
}
