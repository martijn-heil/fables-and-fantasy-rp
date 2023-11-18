package com.fablesfantasyrp.plugin.time

enum class Season(private val displayName: String) {
	LILITHS_VEIL("Lilith's Veil"),
	THE_EMERALD_DUSK("The Emerald Dusk"),
	EDENS_SHINE("Eden's Shine"),
	THE_AMBER_DAWN("The Amber Dawn");

	override fun toString() = displayName

	companion object {
		fun ofYear(year: Int) = values()[year % values().size]
	}
}
