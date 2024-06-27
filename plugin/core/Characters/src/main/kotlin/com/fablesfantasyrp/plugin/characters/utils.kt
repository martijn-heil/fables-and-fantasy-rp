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
package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.domain.FABLES_ADMIN
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.time.formatDateLong
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.utils.Services
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToLong

val Character.isStaffCharacter get() = this.profile.owner == FABLES_ADMIN
val Profile.isStaffCharacter get() = this.owner == FABLES_ADMIN

suspend fun Profile.displayName(): String {
	val characters = Services.get<CharacterRepository>()
	val profileManager = Services.get<ProfileManager>()
	val character = characters.forProfile(this)
	val player = profileManager.getCurrentForProfile(this)
	val suffix = if (player != null) " (${player.name})" else ""
	return if (character != null) {
		"${character.name}$suffix"
	} else {
		"#${this.id}$suffix"
	}
}

val Character.shortName: String get() = name.substringBefore(" ").substringBefore("-")

suspend fun Profile.shortName(): String {
	val characters = Services.get<CharacterRepository>()
	val character = characters.forProfile(this)
	return character?.shortName ?: "#${this.id}"
}

fun calculateDateOfNaturalDeath(dateOfBirth: FablesLocalDate, medianAge: Int): FablesLocalDate {
	return dateOfBirth.plusYears(Random().nextGaussian(medianAge.toDouble(), 0.075 * medianAge).roundToLong())
}

fun sendDyingNotification(player: Player, character: Character) {
	assert(character.dateOfNaturalDeath != null)

	player.sendMessage(miniMessage.deserialize("<prefix> <red>" +
		"<bold>You are dying of old age!</bold> You will die on <underlined><date></underlined>." +
		"</red>",
		Placeholder.component("prefix", legacyText(SYSPREFIX)),
		Placeholder.unparsed("date", formatDateLong(character.dateOfNaturalDeath!!))))
}
