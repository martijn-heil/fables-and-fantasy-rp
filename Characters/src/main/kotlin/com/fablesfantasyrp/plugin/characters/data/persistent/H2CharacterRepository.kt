package com.fablesfantasyrp.plugin.characters.data.persistent

import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

class H2CharacterRepository(private val server: Server,
							private val dataSource: DataSource,
							private val playerInstances: PlayerInstanceRepository) : CharacterRepository, HasDirtyMarker<Character> {
	val TABLE_NAME = "FABLES_CHARACTERS.CHARACTERS"

	override var dirtyMarker: DirtyMarker<Character>? = null

	override fun all(): Collection<Character> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Character>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: Character) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setLong(1, v.id.toLong())
			stmnt.executeUpdate()
		}
	}

	override fun create(v: Character): Character {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, " +
					"name, " +
					"description, " +
					"age, " +
					"race, " +
					"gender, " +
					"created_at, " +
					"last_seen, " +
					"stat_strength, stat_defense, stat_agility, stat_intelligence) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
			stmnt.setLong(1, v.playerInstance.id.toLong())
			stmnt.setString(2, v.name)
			stmnt.setString(3, v.description)
			stmnt.setInt(4, v.age.toInt())
			stmnt.setString(5, v.race.name)
			stmnt.setString(6, v.gender.name)
			stmnt.setTimestamp(7, if (v.createdAt != null) Timestamp.from(v.createdAt) else null)
			stmnt.setTimestamp(8, if (v.lastSeen != null) Timestamp.from(v.lastSeen) else null)
			stmnt.setInt(9, v.stats.strength.toInt())
			stmnt.setInt(10, v.stats.defense.toInt())
			stmnt.setInt(11, v.stats.agility.toInt())
			stmnt.setInt(12, v.stats.intelligence.toInt())
			stmnt.executeUpdate()
			v.dirtyMarker = dirtyMarker
			return v
		}
	}

	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<Character> {
		return playerInstances.forOwner(offlinePlayer).mapNotNull { this.forId(it.id.toULong()) }
	}

	override fun forPlayerInstance(playerInstance: PlayerInstance): Character? {
		return this.forId(playerInstance.id.toULong())
	}

	override fun forName(name: String): Character? {
		TODO("Not yet implemented")
	}

	override fun allNames(): Collection<String> {
		TODO("Not yet implemented")
	}

	override fun forId(id: ULong): Character? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setLong(1, id.toLong())
			val result = stmnt.executeQuery()
			if (!result.next()) {
				return null
			}
			fromRow(result)
		}
	}

	override fun allIds(): Collection<ULong> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<ULong>()
			while (result.next()) all.add(result.getLong("id").toULong())
			all
		}
	}

	override fun update(v: Character) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"name = ?, " +
					"description = ?, " +
					"age = ?, " +
					"race = ?, " +
					"gender = ?, " +
					"created_at = ?, " +
					"last_seen = ?, " +
					"is_dead = ?, " +
					"stat_strength = ?, " +
					"stat_defense = ?, " +
					"stat_agility = ?, " +
					"stat_intelligence = ? " +
					"WHERE id = ?")
			stmnt.setString(1, v.name)
			stmnt.setString(2, v.description)
			stmnt.setInt(3, v.age.toInt())
			stmnt.setString(4, v.race.name)
			stmnt.setString(5, v.gender.name)
			if (v.createdAt != null) stmnt.setTimestamp(6, Timestamp.from(v.createdAt))
			if (v.lastSeen != null) stmnt.setTimestamp(7, Timestamp.from(v.lastSeen))
			stmnt.setBoolean(8, v.isDead)
			stmnt.setInt(9, v.stats.strength.toInt())
			stmnt.setInt(10, v.stats.defense.toInt())
			stmnt.setInt(11, v.stats.agility.toInt())
			stmnt.setInt(12, v.stats.intelligence.toInt())
			stmnt.setInt(13, v.stats.strength.toInt())
			stmnt.setLong(14, v.id.toLong())
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(result: ResultSet): Character {
		val id = result.getLong("id").toULong()
		val name = result.getString("name")
		val age = result.getInt("age").toUInt()
		val description = result.getString("description")
		val gender = Gender.valueOf(result.getString("gender"))
		val race = Race.valueOf(result.getString("race"))
		val statStrength = result.getInt("stat_strength").toUInt()
		val statDefense = result.getInt("stat_defense").toUInt()
		val statAgility = result.getInt("stat_agility").toUInt()
		val statIntelligence = result.getInt("stat_intelligence").toUInt()
		val createdAt = result.getTimestamp("created_at")?.toInstant()
		val lastSeen = result.getTimestamp("last_seen")?.toInstant()
		val isDead = result.getBoolean("is_dead")

		return Character(
				id = id,
				name = name,
				race = race,
				gender = gender,
				age = age,
				description = description,
				createdAt = createdAt,
				lastSeen = lastSeen,
				stats = CharacterStats(statStrength, statDefense, statAgility, statIntelligence),
				isDead = isDead,
				playerInstance = playerInstances.forId(id.toInt())!!,
				dirtyMarker = dirtyMarker
		)
	}
}
