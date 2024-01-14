package com.fablesfantasyrp.plugin.activity.domain.entity

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.model.Named
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegionIdentifier
import com.fablesfantasyrp.plugin.worldguardinterop.toBlockVector3
import org.bukkit.GameMode

class ActivityRegion(override val name: String,
					 val regionIdentifier: WorldGuardRegionIdentifier,
					 val region: WorldGuardRegion?,
					 override val id: String = "") : Identifiable<String>, Named {

	fun countPlayers(): Int {
		if (region == null) return 0

		return region.world.players
			.asSequence()
			.filter { !it.isVanished && it.gameMode != GameMode.SPECTATOR }
			.filter { region.region.contains(it.location.toBlockVector3()) }
			.count()
	}
}
