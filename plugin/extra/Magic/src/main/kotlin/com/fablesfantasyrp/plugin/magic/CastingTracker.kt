package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.domain.entity.Character

class CastingTracker {
	private val busy = HashSet<Character>()

	fun setIsCasting(who: Character, value: Boolean) {
		if (value) {
			busy.add(who)
		} else {
			busy.remove(who)
		}
	}

	fun isCasting(who: Character) = busy.contains(who)
}
