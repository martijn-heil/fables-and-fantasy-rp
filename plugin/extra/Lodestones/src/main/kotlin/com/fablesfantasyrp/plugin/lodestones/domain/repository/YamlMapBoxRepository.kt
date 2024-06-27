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
package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.*
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.fablesfantasyrp.plugin.worldguardinterop.toBlockVector3
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.Configuration
import org.bukkit.plugin.Plugin

class YamlMapBoxRepository(private val plugin: Plugin,
						   private val config: Configuration,
						   private val regions: RegionContainer) : SimpleMapRepository<Int, MapBox>(), MapBoxRepository {
	private val server = plugin.server

	fun init() {
		val mapBoxes = config.getConfigurationSection("map_boxes")!!
			mapBoxes.getKeys(false)
			.map { mapBoxes.getConfigurationSection(it)!! }
			.map { mapBoxConfig ->
				val locationX = mapBoxConfig.getDouble("location.x")
				val locationY = mapBoxConfig.getDouble("location.y")
				val locationZ = mapBoxConfig.getDouble("location.z")
				val locationYaw = mapBoxConfig.getDouble("location.yaw").toFloat()
				val locationPitch = mapBoxConfig.getDouble("location.pitch").toFloat()
				val locationWorld = server.getWorld(mapBoxConfig.getString("location.world")!!)!!
				val location = Location(locationWorld, locationX, locationY, locationZ, locationYaw, locationPitch)
				val regionName = mapBoxConfig.getString("region")
				val region = regions.get(BukkitAdapter.adapt(locationWorld))!!.getRegion(regionName)!!

				val leftTopX = mapBoxConfig.getDouble("plane.left_top.x").toFloat()
				val leftTopZ = mapBoxConfig.getDouble("plane.left_top.z").toFloat()
				val coordinates = PlanarCoordinates(leftTopX, leftTopZ)
				val width = mapBoxConfig.getInt("plane.width")
				val height = mapBoxConfig.getInt("plane.height")
				val planarBounds = PlanarBounds(coordinates, width, height)

				val anchorWorld = server.getWorld(mapBoxConfig.getString("anchor_plane.world")!!)!!
				val anchorX = mapBoxConfig.getDouble("anchor_plane.left_top.x").toFloat()
				val anchorZ = mapBoxConfig.getDouble("anchor_plane.left_top.z").toFloat()
				val anchorPlaneWidth = mapBoxConfig.getInt("anchor_plane.width")
				val anchorPlaneHeight = mapBoxConfig.getInt("anchor_plane.height")
				val anchorCoordinates = PlanarCoordinates(anchorX, anchorZ)
				val anchorPlaneBounds = PlanarBounds(anchorCoordinates, anchorPlaneWidth, anchorPlaneHeight)
				val planarAnchor = PlanarAnchor(anchorWorld, anchorPlaneBounds)

				val plane = AnchoredPlane(planarBounds, planarAnchor)

				assert(plane.anchor.bounds.width != 0)
				assert(plane.anchor.bounds.height != 0)
				assert(plane.bounds.width != 0)
				assert(plane.bounds.height != 0)

				MapBox(0, WorldGuardRegion(locationWorld, region), location, plane)
			}.forEach { this.create(it) }
	}

	override fun forWorld(world: World): MapBox? {
		return this.all().firstOrNull()
	}

	override fun anyContains(location: Location): Boolean {
		return all().any { box -> box.region.region.contains(location.toBlockVector3()) }
	}

	override fun forLocation(location: Location): MapBox? {
		return all().firstOrNull { box -> box.region.region.contains(location.toBlockVector3()) }
	}
}
