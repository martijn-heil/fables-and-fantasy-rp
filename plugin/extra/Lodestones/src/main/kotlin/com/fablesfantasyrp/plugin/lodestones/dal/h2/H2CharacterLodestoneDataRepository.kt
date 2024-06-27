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
package com.fablesfantasyrp.plugin.lodestones.dal.h2

import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.lodestones.dal.repository.CharacterLodestoneDataRepository
import org.bukkit.plugin.Plugin
import javax.sql.DataSource

class H2CharacterLodestoneDataRepository(private val plugin: Plugin,
										 private val dataSource: DataSource) : CharacterLodestoneDataRepository {
	private val TABLE_NAME = "FABLES_LODESTONES.CHARACTER_LODESTONE"

	override fun forCharacter(characterId: Int): Set<Int> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE character_id = ?").apply {
				this.setInt(1, characterId)
			}.executeQuery().asSequence().map { it.getInt("lodestone_id") }.toSet()
		}
	}

	override fun add(characterId: Int, lodestoneId: Int): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("INSERT INTO $TABLE_NAME (character_id, lodestone_id) VALUES (?, ?)").apply {
				this.setInt(1, characterId)
				this.setInt(2, lodestoneId)
			}.executeUpdate()
		}
	}

	override fun remove(characterId: Int, lodestoneId: Int): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE character_id = ? AND lodestone_id = ?").apply {
				this.setInt(1, characterId)
				this.setInt(2, lodestoneId)
			}.executeUpdate()
		}
	}

	override fun destroy(lodestoneId: Int): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE lodestone_id = ?").apply {
				this.setInt(1, lodestoneId)
			}.executeUpdate()
		}
	}
}
