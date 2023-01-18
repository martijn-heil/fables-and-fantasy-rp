package com.fablesfantasyrp.plugin.profile.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import org.bukkit.OfflinePlayer

interface ProfileData : Identifiable<Int> {
	val isActive: Boolean
	val description: String?
	val owner: OfflinePlayer
}
