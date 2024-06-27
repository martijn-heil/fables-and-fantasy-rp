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
package com.fablesfantasyrp.plugin.profile.data.persistent

import com.fablesfantasyrp.plugin.database.getUuid
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.setUuid
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

class H2ProfileRepository(private val plugin: Plugin,
						  private val dataSource: DataSource) : ProfileRepository, HasDirtyMarker<Profile> {
	override var dirtyMarker: DirtyMarker<Profile>? = null
	private val TABLE_NAME = "FABLES_PROFILE.PROFILE"
	private val server = plugin.server

	override fun allForOwner(offlinePlayer: OfflinePlayer?): Collection<Profile> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE owner = ?")
			stmnt.setUuid(1, offlinePlayer?.uniqueId)
			val result = stmnt.executeQuery()
			val results = ArrayList<Profile>()
			while (result.next()) {
				results.add(fromRow(result))
			}
			results
		}
	}

	override fun all(): Collection<Profile> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Profile>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: Profile) = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: Profile): Profile = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			if (v.id == -1) {
				val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
						"(owner, description, active) " +
						"VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
				stmnt.setUuid(1, v.owner?.uniqueId)
				stmnt.setString(2, v.description)
				stmnt.setBoolean(3, v.isActive)
				stmnt.executeUpdate()
				val rs = stmnt.generatedKeys
				rs.next()
				val id = rs.getInt(1)
				Profile(
						id = id,
						owner = v.owner,
						description = v.description,
						isActive = v.isActive,
						dirtyMarker = dirtyMarker
				)
			} else {
				val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
						"(id, owner, description, active) " +
						"VALUES (?, ?, ?, ?)")
				stmnt.setInt(1, v.id)
				stmnt.setUuid(2, v.owner?.uniqueId)
				stmnt.setString(3, v.description)
				stmnt.setBoolean(4, v.isActive)
				stmnt.executeUpdate()

				val stmnt2 = connection.prepareStatement("ALTER TABLE $TABLE_NAME ALTER COLUMN id RESTART WITH (SELECT MAX(id) FROM $TABLE_NAME) + 1")
				stmnt2.executeUpdate()

				Profile(
						id = v.id,
						owner = v.owner,
						description = v.description,
						isActive = v.isActive,
						dirtyMarker = dirtyMarker
				)
			}
		}
	}

	override fun update(v: Profile): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"owner = ?, " +
					"description = ?, " +
					"active = ? " +
					"WHERE id = ?")
			stmnt.setUuid(1, v.owner?.uniqueId)
			stmnt.setString(2, v.description)
			stmnt.setBoolean(3, v.isActive)
			stmnt.setInt(4, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun createOrUpdate(v: Profile): Profile {
		throw NotImplementedError()
	}

	override fun forId(id: Int): Profile? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun allIds(): Collection<Int> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Int>()
			while (result.next()) all.add(result.getInt("id"))
			all
		}
	}

	private fun fromRow(row: ResultSet): Profile {
		val id = row.getInt("id")
		val owner = row.getUuid("owner")?.let { server.getOfflinePlayer(it) }
		val description = row.getString("description")
		val isAccessible = row.getBoolean("active")

		return Profile(
				id = id,
				owner = owner,
				description = description,
				isActive = isAccessible,
				dirtyMarker = dirtyMarker
		)
	}
}
