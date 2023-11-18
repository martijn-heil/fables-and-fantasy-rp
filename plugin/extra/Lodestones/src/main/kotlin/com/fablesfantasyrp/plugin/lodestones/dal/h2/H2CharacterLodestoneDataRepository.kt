package com.fablesfantasyrp.plugin.lodestones.dal.h2

import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.lodestones.dal.repository.CharacterLodestoneDataRepository
import javax.sql.DataSource

class H2CharacterLodestoneDataRepository(private val dataSource: DataSource) : CharacterLodestoneDataRepository {
	private val TABLE_NAME = "FABLES_LODESTONES.CHARACTER_LODESTONE"

	override fun forCharacter(characterId: Int): Set<Int> {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE character_id = ?").apply {
				this.setInt(1, characterId)
			}.executeQuery().asSequence().map { it.getInt("lodestone_id") }.toSet()
		}
	}

	override fun add(characterId: Int, lodestoneId: Int) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("INSERT INTO $TABLE_NAME (character_id, lodestone_id) VALUES (?, ?)").apply {
				this.setInt(1, characterId)
				this.setInt(2, lodestoneId)
			}.executeUpdate()
		}
	}

	override fun remove(characterId: Int, lodestoneId: Int) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE character_id = ? AND lodestone_id = ?").apply {
				this.setInt(1, characterId)
				this.setInt(2, lodestoneId)
			}.executeUpdate()
		}
	}

	override fun destroy(lodestoneId: Int) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE lodestone_id = ?").apply {
				this.setInt(1, lodestoneId)
			}.executeUpdate()
		}
	}
}
