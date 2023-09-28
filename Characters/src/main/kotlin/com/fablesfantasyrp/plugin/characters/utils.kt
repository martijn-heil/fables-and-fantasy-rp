package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizen.objects.PlayerTag
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.denizenParseTag
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.FABLES_ADMIN
import com.fablesfantasyrp.plugin.utils.Services
import org.bukkit.entity.Player

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
