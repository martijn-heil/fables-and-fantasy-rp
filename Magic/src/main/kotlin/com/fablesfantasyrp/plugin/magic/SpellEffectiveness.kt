package com.fablesfantasyrp.plugin.magic

enum class SpellEffectiveness(val displayName: String) {
	CRITICAL_FAILURE("critical failure"),
	SUCCESS("success"),
	CRITICAL_SUCCESS("critical success");

	companion object {
		fun fromRoll(roll: Int) = when {
					roll <= 6 -> CRITICAL_FAILURE
					roll in 7..15 -> SUCCESS
					roll >= 16 -> CRITICAL_SUCCESS
					else -> throw IllegalStateException()
				}
	}
}
