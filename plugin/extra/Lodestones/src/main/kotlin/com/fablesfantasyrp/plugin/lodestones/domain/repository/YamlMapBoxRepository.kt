package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.MapBox
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Location
import org.bukkit.configuration.Configuration
import org.bukkit.plugin.Plugin

class YamlMapBoxRepository(private val plugin: Plugin,
						   private val config: Configuration,
						   private val regions: RegionContainer) : SimpleMapRepository<Int, MapBox>(), MapBoxRepository {
	private val server = plugin.server

	fun init() {
		config.getKeys(false)
			.mapNotNull { config.getConfigurationSection(it) }
			.map { mapBoxConfig ->
				val locationX = mapBoxConfig.getDouble("location.x")
				val locationY = mapBoxConfig.getDouble("location.y")
				val locationZ = mapBoxConfig.getDouble("location.z")
				val locationWorld = server.getWorld(mapBoxConfig.getString("location.world")!!)!!
				val location = Location(locationWorld, locationX, locationY, locationZ)
				val regionName = mapBoxConfig.getString("region")
				val region = regions.get(BukkitAdapter.adapt(locationWorld))!!.getRegion(regionName)!!

				MapBox(0, WorldGuardRegion(locationWorld, region), location)
			}.forEach { this.create(it) }
	}
}
