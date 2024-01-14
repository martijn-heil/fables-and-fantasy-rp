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
