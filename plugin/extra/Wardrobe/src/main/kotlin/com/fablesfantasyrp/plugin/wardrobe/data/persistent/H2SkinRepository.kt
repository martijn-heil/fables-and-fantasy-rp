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
package com.fablesfantasyrp.plugin.wardrobe.data.persistent

import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.wardrobe.data.Skin
import com.fablesfantasyrp.plugin.wardrobe.data.SkinRepository
import org.bukkit.plugin.Plugin
import org.bukkit.profile.PlayerTextures.SkinModel
import java.net.URL
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class H2SkinRepository(private val plugin: Plugin,
					   private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, Skin>(Int::class.java, plugin, dataSource), SkinRepository {
	private val server = plugin.server

	override val TABLE_NAME = "FABLES_WARDROBE.SKIN"

	override fun create(v: Skin): Skin = warnBlockingIO(plugin) {
		val profile = server.createProfile(UUID.randomUUID(), null)
		profile.setProperty(v.toProfileProperty())
		val skinUrl = profile.textures.skin!!
		val skinModel = profile.textures.skinModel

		dataSource.connection.use { connection ->
			connection.prepareStatement("MERGE INTO $TABLE_NAME " +
					"(" +
					"textures_value, " +
					"textures_signature, " +
					"textures_skin_url, " +
					"textures_skin_model" +
					") " +
					"KEY(textures_skin_url, textures_skin_model) " +
					"VALUES (?, ?, ?, ?)").apply {

				this.setString(1, v.value)
				this.setString(2, v.signature)
				this.setString(3, skinUrl.toString())
				this.setString(4, skinModel.name)
			}.executeUpdate()

			forValue(skinUrl, skinModel)
		}
	}

	override fun update(v: Skin) {
		throw UnsupportedOperationException()
	}

	override fun createOrUpdate(v: Skin): Skin {
		return create(v)
	}

	override fun forId(id: Int): Skin? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return@use null
			fromRow(result)
		}
	}

	override fun forValue(skinUrl: URL, skinModel: SkinModel): Skin = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE " +
				"textures_skin_url = ? AND textures_skin_model = ?").apply {
				this.setString(1, skinUrl.toString())
				this.setString(2, skinModel.name)
			}.executeQuery().asSequence().first().let { fromRow(it) }
		}
	}

	override fun fromRow(row: ResultSet): Skin {
		val id = row.getInt("id")
		val value = row.getString("textures_value")
		val signature = row.getString("textures_signature")

		return Skin(
			id = id,
			value = value,
			signature = signature,
		)
	}
}
