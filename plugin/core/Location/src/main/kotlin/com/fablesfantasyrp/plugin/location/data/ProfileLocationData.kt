package com.fablesfantasyrp.plugin.location.data

import com.fablesfantasyrp.plugin.database.model.Identifiable
import org.bukkit.Location

interface ProfileLocationData : Identifiable<Int> {
	val location: Location
}
