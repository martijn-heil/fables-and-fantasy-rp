package com.fablesfantasyrp.plugin.location.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import org.bukkit.Location

interface PlayerInstanceLocationData : Identifiable<Int> {
	val location: Location
}
