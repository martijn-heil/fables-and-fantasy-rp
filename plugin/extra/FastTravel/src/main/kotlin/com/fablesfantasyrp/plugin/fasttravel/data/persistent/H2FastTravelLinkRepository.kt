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
package com.fablesfantasyrp.plugin.fasttravel.data.persistent

import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelLink
import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelLinkRepository
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import javax.sql.DataSource
import kotlin.time.Duration.Companion.seconds

class H2FastTravelLinkRepository(private val plugin: Plugin,
								 private val dataSource: DataSource,
								 private val regionContainer: RegionContainer) : FastTravelLinkRepository, HasDirtyMarker<FastTravelLink> {
	override var dirtyMarker: DirtyMarker<FastTravelLink>? = null
	private val TABLE_NAME = "FABLES_FASTTRAVEL.FASTTRAVEL"
	private val server = plugin.server

	override fun forOriginRegion(region: WorldGuardRegion): FastTravelLink? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE from_region = ?")
			stmnt.setString(1, "${region.region.id},${region.world.uid}")
			val result = stmnt.executeQuery()
			if (!result.next()) return@use null
			fromRowOrDelete(result, connection)
		}
	}

	override fun all(): Collection<FastTravelLink> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<FastTravelLink>()
			while (result.next()) {
				val parsed = fromRowOrDelete(result, connection)
				if (parsed != null) all.add(parsed)
			}
			all
		}
	}

	override fun destroy(v: FastTravelLink) = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: FastTravelLink): FastTravelLink = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(from_region, to_x, to_y, to_z, to_yaw, to_pitch, to_world, travel_duration) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
			stmnt.setString(1, "${v.from.region.id},${v.from.world.uid}")
			stmnt.setDouble(2, v.to.x)
			stmnt.setDouble(3, v.to.y)
			stmnt.setDouble(4, v.to.z)
			stmnt.setFloat(5, v.to.yaw)
			stmnt.setFloat(6, v.to.pitch)
			stmnt.setObject(7, v.to.world.uid)
			stmnt.setInt(8, v.travelDuration.inWholeSeconds.toInt())
			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			FastTravelLink(
					id = id,
					from = v.from,
					to = v.to,
					travelDuration = v.travelDuration,
					dirtyMarker = dirtyMarker
			)
		}
	}

	override fun update(v: FastTravelLink) {
		throw NotImplementedError()
	}

	override fun createOrUpdate(v: FastTravelLink): FastTravelLink {
		throw NotImplementedError()
	}

	override fun forId(id: Int): FastTravelLink? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return@use null
			fromRowOrDelete(result, connection)
		}
	}

	override fun allIds(): Collection<Int> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Int>()
			while (result.next()) all.add(result.getInt("id"))
			all
		}
	}

	private fun fromRow(row: ResultSet): FastTravelLink? {
		val id = row.getInt("id")
		val fromRegionString = row.getString("from_region")
		val fromRegionStringSplit = fromRegionString.split(",")
		val regionName = fromRegionStringSplit[0]
		val fromWorld = server.getWorld(UUID.fromString(fromRegionStringSplit[1])) ?: return null
		val fromRegion = regionContainer.get(BukkitAdapter.adapt(fromWorld))?.getRegion(regionName) ?: return null
		val travelDuration = row.getInt("travel_duration").seconds

		val toX = row.getDouble("to_x")
		val toY = row.getDouble("to_y")
		val toZ = row.getDouble("to_z")
		val toYaw = row.getFloat("to_yaw")
		val toPitch = row.getFloat("to_pitch")
		val toWorld = server.getWorld(row.getObject("to_world", UUID::class.java)) ?: return null
		val toLocation = Location(toWorld, toX, toY, toZ, toYaw, toPitch)

		return FastTravelLink(from = WorldGuardRegion(fromWorld, fromRegion), to = toLocation, travelDuration = travelDuration, id = id)
	}

	private fun fromRowOrDelete(row: ResultSet, connection: Connection): FastTravelLink? {
		val parsed = fromRow(row)
		return if (parsed != null) {
			parsed
		} else {
			val id = row.getInt("id")
			val deleteStmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			deleteStmnt.setInt(1, id)
			deleteStmnt.executeUpdate()
			null
		}
	}
}
