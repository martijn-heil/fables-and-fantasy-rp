package com.fablesfantasyrp.plugin.staffmode

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class StaffModePlaceholderExpansion : PlaceholderExpansion() {
	override fun getIdentifier() = "staffmode"

	override fun getAuthor() = "Ninjoh"
	override fun getVersion() = "1.0.0"
	override fun persist() = true

	override fun onPlaceholderRequest(p: Player?, params: String): String? {
		if (p == null) return null
		return when (params) {
			"isonduty" -> p.isOnDuty.toString()
			else -> null
		}
	}
}
