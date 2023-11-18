package com.fablesfantasyrp.plugin.fasttravel.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import org.bukkit.Location
import kotlin.time.Duration

interface FastTravelLinkData : Identifiable<Int> {
	val from: WorldGuardRegion
	val to: Location
	val travelDuration: Duration
}
