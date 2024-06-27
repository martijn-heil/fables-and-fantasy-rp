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
package com.fablesfantasyrp.plugin.staffprofiles.dal.h2

import com.fablesfantasyrp.plugin.database.setCollection
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.staffprofiles.dal.repository.StaffProfileDataRepository
import org.bukkit.plugin.Plugin
import org.h2.api.H2Type
import java.sql.ResultSet
import javax.sql.DataSource

class H2StaffProfileDataRepository(private val plugin: Plugin,
								   private val dataSource: DataSource)
	: StaffProfileDataRepository {
	private val TABLE_NAME = "FABLES_STAFFPROFILES.STAFF_PROFILES"

	override fun create(v: Int): Int = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (profile) VALUES (?)")
			stmnt.setInt(1, v)
			stmnt.executeUpdate()
			v
		}
	}

	override fun update(v: Int) { throw NotImplementedError() }
	override fun createOrUpdate(v: Int): Int = create(v)

	override fun all(): Collection<Int> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Int>()
			while (result.next()) {
				val parsed = fromRow(result)
				all.add(parsed)
			}
			all
		}
	}

	override fun contains(v: Int): Boolean = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile = ?").apply {
				this.setInt(1, v)
			}.executeQuery().next()
		}
	}

	override fun containsAny(v: Collection<Int>): Boolean = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile IN ?").apply {
				this.setCollection(1, H2Type.INTEGER, v)
			}.executeQuery().next()
		}
	}

	override fun destroy(v: Int): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE profile = ?")
			stmnt.setInt(1, v)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): Int = row.getInt("profile")
}
