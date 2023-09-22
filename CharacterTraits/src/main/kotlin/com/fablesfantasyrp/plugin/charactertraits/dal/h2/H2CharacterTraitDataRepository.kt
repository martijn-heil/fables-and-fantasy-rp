package com.fablesfantasyrp.plugin.charactertraits.dal.h2

import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.charactertraits.dal.model.CharacterTraitData
import com.fablesfantasyrp.plugin.charactertraits.dal.repository.CharacterTraitDataRepository
import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import java.sql.ResultSet
import javax.sql.DataSource

class H2CharacterTraitDataRepository(private val dataSource: DataSource)
	: CharacterTraitDataRepository, BaseH2KeyedRepository<String, CharacterTraitData>(String::class.java, dataSource) {
	private val SCHEMA = "FABLES_CHARACTER_TRAITS"
	override val TABLE_NAME = "$SCHEMA.CHARACTER_TRAIT"

	override fun update(v: CharacterTraitData) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("UPDATE $TABLE_NAME SET display_name = ?, description = ? WHERE id = ?").apply {
				this.setString(1, v.displayName)
				this.setString(2, v.description)
				this.setString(3, v.id)
			}.executeUpdate()
		}
	}

	override fun create(v: CharacterTraitData): CharacterTraitData {
		dataSource.connection.use { connection ->
			connection.prepareStatement("INSERT INTO $TABLE_NAME (id, display_name, description) VALUES (?, ?, ?)").apply {
				this.setString(1, v.id)
				this.setString(2, v.displayName)
				this.setString(3, v.description)
			}.executeUpdate()
		}

		return v
	}

	override fun forRace(race: Race): Collection<CharacterTraitData> {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME " +
				"JOIN $SCHEMA.RACE_CHARACTER_TRAIT ON " +
				"$SCHEMA.RACE_CHARACTER_TRAIT.character_trait_id = $TABLE_NAME.id AND " +
				"$SCHEMA.RACE_CHARACTER_TRAIT.race = ?").apply {
				this.setString(1, race.name)
			}.executeQuery().asSequence().map { fromRow(it) }.toList()
		}
	}

	override fun forCharacter(character: Character): Collection<CharacterTraitData> {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME " +
				"JOIN $SCHEMA.CHARACTER_CHARACTER_TRAIT ON " +
				"$SCHEMA.CHARACTER_CHARACTER_TRAIT.character_trait_id = $TABLE_NAME.id AND " +
				"$SCHEMA.CHARACTER_CHARACTER_TRAIT.character_id = ?").apply {
				this.setInt(1, character.id)
			}.executeQuery().asSequence().map { fromRow(it) }.toList()
		}
	}

	override fun linkToRace(race: Race, trait: CharacterTraitData) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("MERGE INTO $SCHEMA.RACE_CHARACTER_TRAIT (race, character_trait_id) " +
				"KEY (race, character_trait_id)" +
				"VALUES (?, ?)").apply {
				this.setString(1, race.name)
				this.setString(2, trait.id)
			}.executeUpdate()
		}
	}

	override fun unlinkFromRace(race: Race, trait: CharacterTraitData) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("DELETE FROM $SCHEMA.RACE_CHARACTER_TRAIT WHERE " +
				"race = ? AND character_trait_id = ?").apply {
				this.setString(1, race.name)
				this.setString(2, trait.id)
			}.executeUpdate()
		}
	}

	override fun linkToCharacter(characterId: Int, trait: CharacterTraitData) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("MERGE INTO $SCHEMA.CHARACTER_CHARACTER_TRAIT (character_id, character_trait_id) " +
				"KEY (character_id, character_trait_id)" +
				"VALUES (?, ?)").apply {
				this.setInt(1, characterId)
				this.setString(2, trait.id)
			}.executeUpdate()
		}
	}

	override fun unlinkFromCharacter(characterId: Int, trait: CharacterTraitData) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("DELETE FROM $SCHEMA.CHARACTER_CHARACTER_TRAIT WHERE " +
				"character_id = ? AND character_trait_id = ?").apply {
				this.setInt(1, characterId)
				this.setString(2, trait.id)
			}.executeUpdate()
		}
	}

	override fun fromRow(row: ResultSet): CharacterTraitData
		= CharacterTraitData(row.getString("id"), row.getString("display_name"), row.getString("description"))
}
