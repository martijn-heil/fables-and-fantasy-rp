/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
