package com.fablesfantasyrp.plugin.magic

enum class SpellEffectiveness(val displayName: String) {
	CRITICAL_FAILURE("critical failure"),
	SUCCESS("success"),
	CRITICAL_SUCCESS("critical success");

	companion object {
		fun fromRoll(roll: UInt) = when {
					roll <= 6U -> CRITICAL_FAILURE
					roll in 7U..15U -> SUCCESS
					roll >= 16U -> CRITICAL_SUCCESS
					else -> throw IllegalStateException()
				}
	}
}
