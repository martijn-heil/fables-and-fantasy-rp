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
package com.fablesfantasyrp.plugin.activity.dal.yaml

import com.fablesfantasyrp.plugin.activity.dal.model.ActivityRegionData
import com.fablesfantasyrp.plugin.activity.dal.repository.ActivityRegionDataRepository
import com.fablesfantasyrp.plugin.domain.EDEN
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegionIdentifier
import org.bukkit.configuration.file.FileConfiguration

class YamlActivityRegionDataRepository(private val file: FileConfiguration) : ActivityRegionDataRepository {
	override fun forId(id: String): ActivityRegionData? {
		val section = file.getConfigurationSection("regions")!!.getConfigurationSection(id) ?: return null
		val displayName = section.getString("display_name")!!
		val regionName = section.getString("region")!!
		val regionIdentifier = WorldGuardRegionIdentifier(EDEN!!.name, regionName)

		return ActivityRegionData(
			id = id,
			region = regionIdentifier,
			displayName = displayName,
		)
	}

	override fun allIds(): Collection<String> {
		return file.getConfigurationSection("regions")!!.getKeys(false)
	}

	override fun all(): Collection<ActivityRegionData> {
		return allIds().map { forId(it)!! }
	}
}
