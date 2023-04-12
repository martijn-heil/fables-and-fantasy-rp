package com.fablesfantasyrp.plugin.characters.data.persistent

import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import java.sql.ResultSet
import java.time.Instant
import javax.sql.DataSource

class H2CharacterRepository(private val server: Server,
							private val dataSource: DataSource,
							private val profiles: ProfileRepository) : CharacterRepository, HasDirtyMarker<Character> {
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
			stmnt.setInt(1, v.id)
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
					"changed_stats_at, " +
					"last_seen, " +
					"stat_strength, stat_defense, stat_agility, stat_intelligence) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
			stmnt.setInt(1, v.profile.id)
			stmnt.setString(2, v.name)
			stmnt.setString(3, v.description)
			stmnt.setInt(4, v.age.toInt())
			stmnt.setString(5, v.race.name)
			stmnt.setString(6, v.gender.name)
			stmnt.setObject(7, v.createdAt)
			stmnt.setObject(8, v.changedStatsAt)
			stmnt.setObject(9, v.lastSeen)
			stmnt.setInt(10, v.stats.strength.toInt())
			stmnt.setInt(11, v.stats.defense.toInt())
			stmnt.setInt(12, v.stats.agility.toInt())
			stmnt.setInt(13, v.stats.intelligence.toInt())
			stmnt.executeUpdate()
			v.dirtyMarker = dirtyMarker
			return Character(
					id = v.profile.id,
					name = v.name,
					description = v.description,
					age = v.age,
					race = v.race,
					gender = v.gender,
					createdAt = v.createdAt,
					lastSeen = v.lastSeen,
					stats = v.stats,
					profile = v.profile,
					dirtyMarker = dirtyMarker
			)
		}
	}

	override fun forOwner(offlinePlayer: OfflinePlayer?): Collection<Character> {
		return profiles.allForOwner(offlinePlayer).mapNotNull { this.forId(it.id) }
	}

	override fun forProfile(profile: Profile): Character? {
		return this.forId(profile.id)
	}

	override fun forName(name: String): Character? {
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

	override fun forId(id: Int): Character? {
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

	override fun update(v: Character) {
		server.logger.info("update(Character)")
		dataSource.connection.use { connection ->
			server.logger.info("update(Character) prepareStatement()")
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"name = ?, " +
					"description = ?, " +
					"age = ?, " +
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
			server.logger.info("update(Character) 1")
			stmnt.setString(1, v.name)
			server.logger.info("update(Character) 2")
			stmnt.setString(2, v.description)
			server.logger.info("update(Character) 3")
			stmnt.setInt(3, v.age.toInt())
			server.logger.info("update(Character) 4")
			stmnt.setString(4, v.race.name)
			server.logger.info("update(Character) 5")
			stmnt.setString(5, v.gender.name)
			server.logger.info("update(Character) 6")
			stmnt.setObject(6, v.createdAt)
			server.logger.info("update(Character) 7")
			stmnt.setObject(7, v.lastSeen)
			server.logger.info("update(Character) 8")
			stmnt.setBoolean(8, v.isDead)
			server.logger.info("update(Character) 9")
			stmnt.setBoolean(9, v.isShelved)
			server.logger.info("update(Character) 10")
			stmnt.setObject(10, v.diedAt)
			server.logger.info("update(Character) 11")
			stmnt.setObject(11, v.shelvedAt)
			server.logger.info("update(Character) 12")
			stmnt.setObject(12, v.changedStatsAt)
			server.logger.info("update(Character) 13")
			stmnt.setInt(13, v.stats.strength.toInt())
			server.logger.info("update(Character) 14")
			stmnt.setInt(14, v.stats.defense.toInt())
			server.logger.info("update(Character) 15")
			stmnt.setInt(15, v.stats.agility.toInt())
			server.logger.info("update(Character) 16")
			stmnt.setInt(16, v.stats.intelligence.toInt())
			server.logger.info("update(Character) 17")
			stmnt.setInt(17, v.id)
			server.logger.info("update(Character) 18")
			stmnt.executeUpdate()
			server.logger.info("update(Character) 19")
		}
		server.logger.info("update(Character) done!")
	}

	private fun fromRow(result: ResultSet): Character {
		val id = result.getInt("id")
		val name = result.getString("name")
		val age = result.getInt("age").toUInt()
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
				isShelved = isShelved,
				shelvedAt = shelvedAt,
				diedAt = diedAt,
				changedStatsAt = changedStatsAt,
				profile = profiles.forId(id)!!,
				dirtyMarker = dirtyMarker
		)
	}
}
