package com.fablesfantasyrp.plugin.morelogging

enum class ToggleableState {
	ON,
	OFF;

	operator fun not() = when (this) {
		ON -> OFF
		OFF -> ON
	}

	companion object {
		fun fromIsActiveBoolean(b: Boolean) = if (b) ON else OFF
	}
}
