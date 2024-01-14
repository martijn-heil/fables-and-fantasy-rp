package com.fablesfantasyrp.plugin.activity.domain.mapper

import com.fablesfantasyrp.plugin.activity.dal.model.ActivityRegionData
import com.fablesfantasyrp.plugin.activity.dal.repository.ActivityRegionDataRepository
import com.fablesfantasyrp.plugin.activity.domain.entity.ActivityRegion
import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncMappingImmutableRepository
import com.fablesfantasyrp.plugin.database.model.HasCacheMarker
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Server

class ActivityRegionMapper(child: ActivityRegionDataRepository,
						   private val server: Server,
						   private val regionContainer: RegionContainer)
	: AsyncMappingImmutableRepository<String, ActivityRegionData, ActivityRegion, ActivityRegionDataRepository>(child),
	HasCacheMarker<ActivityRegion>
{
	override var cacheMarker: CacheMarker<ActivityRegion>? = null

	override fun convertFromChild(v: ActivityRegionData): ActivityRegion {
		val world = server.getWorld(v.region.worldName)
		val regionManager = world?.let { regionContainer.get(BukkitAdapter.adapt(world)) }
		val protectedRegion = regionManager?.getRegion(v.region.regionName)
		val region = if (world != null && protectedRegion != null) {
			WorldGuardRegion(world, protectedRegion)
		} else null

		return ActivityRegion(
			regionIdentifier = v.region,
			region = region,
			name = v.displayName
		)
	}
}
