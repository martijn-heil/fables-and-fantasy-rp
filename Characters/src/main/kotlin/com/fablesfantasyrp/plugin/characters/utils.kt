package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizen.objects.PlayerTag
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.denizeninterop.denizenParseTag
import com.fablesfantasyrp.plugin.utils.FABLES_ADMIN
import org.bukkit.entity.Player

val Player.characterSlotCount: Int get()
	= denizenParseTag("<proc[characters_calculate_slotcount].context[<[player]>]>",
			mapOf("player" to PlayerTag(this))
	).asElement().asInt()

val Character.isStaffCharacter get() = this.profile.owner == FABLES_ADMIN
