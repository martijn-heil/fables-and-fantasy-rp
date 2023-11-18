package com.fablesfantasyrp.plugin.characters.dal.h2

import com.fablesfantasyrp.plugin.characters.dal.enums.Gender
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.dal.model.CharacterData
import com.fablesfantasyrp.plugin.characters.dal.repository.CharacterDataRepository
import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import org.bukkit.OfflinePlayer
import java.sql.ResultSet
import java.time.Instant
import java.time.temporal.ChronoField
import javax.sql.DataSource

class H2CharacterDataRepository(private val dataSource: DataSource,
								private val profiles: ProfileRepository) : CharacterDataRepository {
	val TABLE_NAME = "FABLES_CHARACTERS.CHARACTERS"

	override fun all(): Collection<CharacterData> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<CharacterData>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: CharacterData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun create(v: CharacterData): CharacterData {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, " +
					"name, " +
					"description, " +
					"date_of_birth_epoch_day, " +
					"date_of_natural_death_epoch_day, " +
					"race, " +
					"gender, " +
					"created_at, " +
					"changed_stats_at, " +
					"last_seen, " +
					"stat_strength, stat_defense, stat_agility, stat_intelligence) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
			stmnt.setInt(1, v.id)
			stmnt.setString(2, v.name)
			stmnt.setString(3, v.description)
			if (v.dateOfBirth != null) stmnt.setLong(4, v.dateOfBirth.getLong(ChronoField.EPOCH_DAY)) else stmnt.setObject(4, null)
			if (v.dateOfNaturalDeath != null) stmnt.setLong(5, v.dateOfNaturalDeath.getLong(ChronoField.EPOCH_DAY)) else stmnt.setObject(5, null)
			stmnt.setString(6, v.race.name)
			stmnt.setString(7, v.gender.name)
			stmnt.setObject(8, v.createdAt)
			stmnt.setObject(9, v.changedStatsAt)
			stmnt.setObject(10, v.lastSeen)
			stmnt.setInt(11, v.stats.strength.toInt())
			stmnt.setInt(12, v.stats.defense.toInt())
			stmnt.setInt(13, v.stats.agility.toInt())
			stmnt.setInt(14, v.stats.intelligence.toInt())
			stmnt.executeUpdate()
			return CharacterData(
				id = v.id,
				name = v.name,
				description = v.description,
				dateOfBirth = v.dateOfBirth,
				dateOfNaturalDeath = v.dateOfNaturalDeath,
				race = v.race,
				gender = v.gender,
				createdAt = v.createdAt,
				lastSeen = v.lastSeen,
				stats = v.stats,
			)
		}
	}

	override fun forOwner(offlinePlayer: OfflinePlayer?): Collection<CharacterData> {
		return profiles.allForOwner(offlinePlayer).mapNotNull { this.forId(it.id) }
	}

	override fun forProfile(profile: Profile): CharacterData? {
		return this.forId(profile.id)
	}

	override fun forName(name: String): CharacterData? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE name = ?")
			stmnt.setString(1, name)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	// stub implementation
	override fun nameExists(name: String): Boolean = this.forName(name) != null

	override val nameMap: Map<String, Int>
		get() {
			return dataSource.connection.use { connection ->
				val stmnt = connection.prepareStatement("SELECT id, name FROM $TABLE_NAME")
				val result = stmnt.executeQuery()
				val map = HashMap<String, Int>()
				while (result.next()) {
					val id = result.getInt("id")
					val name = result.getString("name")
					map[name] = id
				}
				map
			}
		}

	override fun forId(id: Int): CharacterData? {
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

	override fun update(v: CharacterData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"name = ?, " +
					"description = ?, " +
					"date_of_birth_epoch_day = ?, " +
					"date_of_natural_death_epoch_day = ?, " +
					"race = ?, " +
					"gender = ?, " +
					"created_at = ?, " +
					"last_seen = ?, " +
					"is_dead = ?, " +
					"is_shelved = ?, " +
					"died_at = ?, " +
					"shelved_at = ?, " +
					"changed_stats_at = ?, " +
					"stat_strength = ?, " +
					"stat_defense = ?, " +
					"stat_agility = ?, " +
					"stat_intelligence = ? " +
					"WHERE id = ?")
			stmnt.setString(1, v.name)
			stmnt.setString(2, v.description)
			if (v.dateOfBirth != null) stmnt.setLong(3, v.dateOfBirth.getLong(ChronoField.EPOCH_DAY)) else stmnt.setObject(3, null)
			if (v.dateOfNaturalDeath!= null) stmnt.setLong(4, v.dateOfNaturalDeath.getLong(ChronoField.EPOCH_DAY)) else stmnt.setObject(4, null)
			stmnt.setString(5, v.race.name)
			stmnt.setString(6, v.gender.name)
			stmnt.setObject(7, v.createdAt)
			stmnt.setObject(8, v.lastSeen)
			stmnt.setBoolean(9, v.isDead)
			stmnt.setBoolean(10, v.isShelved)
			stmnt.setObject(11, v.diedAt)
			stmnt.setObject(12, v.shelvedAt)
			stmnt.setObject(13, v.changedStatsAt)
			stmnt.setInt(14, v.stats.strength.toInt())
			stmnt.setInt(15, v.stats.defense.toInt())
			stmnt.setInt(16, v.stats.agility.toInt())
			stmnt.setInt(17, v.stats.intelligence.toInt())
			stmnt.setInt(18, v.id)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(result: ResultSet): CharacterData {
		val id = result.getInt("id")
		val name = result.getString("name")
		val dateOfBirth = result.getLong("date_of_birth_epoch_day")
			.let { if (it != 0L) FablesLocalDate.ofEpochDay(it) else null }
		val dateOfNaturalDeath = result.getLong("date_of_natural_death_epoch_day")
			.let { if (it != 0L) FablesLocalDate.ofEpochDay(it) else null }
		val description = result.getString("description")
		val gender = Gender.valueOf(result.getString("gender"))
		val race = Race.valueOf(result.getString("race"))
		val statStrength = result.getInt("stat_strength").toUInt()
		val statDefense = result.getInt("stat_defense").toUInt()
		val statAgility = result.getInt("stat_agility").toUInt()
		val statIntelligence = result.getInt("stat_intelligence").toUInt()
		val createdAt = result.getObject("created_at", Instant::class.java)
		val lastSeen = result.getObject("last_seen", Instant::class.java)
		val shelvedAt = result.getObject("shelved_at", Instant::class.java)
		val diedAt = result.getObject("died_at", Instant::class.java)
		val changedStatsAt = result.getObject("changed_stats_at", Instant::class.java)
		val isDead = result.getBoolean("is_dead")
		val isShelved = result.getBoolean("is_shelved")

		return CharacterData(
			id = id,
			name = name,
			race = race,
			gender = gender,
			dateOfBirth = dateOfBirth,
			dateOfNaturalDeath = dateOfNaturalDeath,
			description = description,
			createdAt = createdAt,
			lastSeen = lastSeen,
			stats = CharacterStats(statStrength, statDefense, statAgility, statIntelligence),
			isDead = isDead,
			isShelved = isShelved,
			shelvedAt = shelvedAt,
			diedAt = diedAt,
			changedStatsAt = changedStatsAt,
		)
	}
}
