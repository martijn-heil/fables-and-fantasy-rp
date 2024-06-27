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
package com.fablesfantasyrp.plugin.economy.data.persistent

import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomy
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import javax.sql.DataSource

class H2ProfileEconomyRepository(private val plugin: Plugin,
								 private val dataSource: DataSource)
	: ProfileEconomyRepository, HasDirtyMarker<ProfileEconomy> {
	override var dirtyMarker: DirtyMarker<ProfileEconomy>? = null
	private val TABLE_NAME = "FABLES_ECONOMY.MONEY"

	override fun forProfile(profile: Profile): ProfileEconomy {
		check(!profile.isDestroyed)

		val economy = this.forId(profile.id) ?: run {
			this.create(ProfileEconomy(profile.id, 0, 0))
		}

		return economy
	}

	override fun all(): Collection<ProfileEconomy> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<ProfileEconomy>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: ProfileEconomy): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: ProfileEconomy): ProfileEconomy = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(" +
					"id, " +
					"pocket_money, " +
					"bank_money " +
					") " +
					"VALUES (?, ?, ?)")
			stmnt.setInt(1, v.id)
			stmnt.setInt(2, v.money)
			stmnt.setInt(3, v.bankMoney)
			stmnt.executeUpdate()
			v.dirtyMarker = dirtyMarker
			v
		}
	}

	override fun update(v: ProfileEconomy): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"pocket_money = ?, " +
					"bank_money = ? " +
				"WHERE id = ?")
			stmnt.setInt(1, v.money)
			stmnt.setInt(2, v.bankMoney)
			stmnt.setInt(3, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun createOrUpdate(v: ProfileEconomy): ProfileEconomy {
		throw NotImplementedError()
	}

	override fun forId(id: Int): ProfileEconomy? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return@use null
			fromRow(result)
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

	private fun fromRow(row: ResultSet): ProfileEconomy {
		val id = row.getInt("id")
		val money = row.getInt("pocket_money")
		val bankMoney = row.getInt("bank_money")

		return ProfileEconomy(
				id = id,
				money = money,
				bankMoney = bankMoney,
				dirtyMarker = dirtyMarker
		)
	}
}
