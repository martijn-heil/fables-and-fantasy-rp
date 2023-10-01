package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizen.objects.PlayerTag
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.denizenParseTag
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.time.formatDateLong
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.utils.FABLES_ADMIN
import com.fablesfantasyrp.plugin.utils.Services
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToLong

val Player.characterSlotCount: Int get()
	= denizenParseTag("<proc[characters_calculate_slotcount].context[<[player]>]>",
			mapOf("player" to PlayerTag(this))
	).asElement().asInt()

val Character.isStaffCharacter get() = this.profile.owner == FABLES_ADMIN
val Profile.isStaffCharacter get() = this.owner == FABLES_ADMIN

val Profile.displayName: String
	get() {
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

val Profile.shortName: String
	get() {
		val characters = Services.get<CharacterRepository>()
		val character = characters.forProfile(this)
		return character?.shortName ?: "#${this.id}"
	}

fun calculateDateOfNaturalDeath(dateOfBirth: FablesLocalDate, medianAge: Int): FablesLocalDate {
	return dateOfBirth.plusYears(Random().nextGaussian(medianAge.toDouble(), 5.0).roundToLong())
}

fun sendDyingNotification(player: Player, character: Character) {
	assert(character.dateOfNaturalDeath != null)

	player.sendMessage(miniMessage.deserialize("<prefix> <red>" +
		"<bold>You are dying of old age!</bold> You will die on <underlined><date></underlined>." +
		"</red>",
		Placeholder.component("prefix", legacyText(SYSPREFIX)),
		Placeholder.unparsed("date", formatDateLong(character.dateOfNaturalDeath!!))))
}
