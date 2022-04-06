package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.repository.CachingRepository
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import java.lang.ref.WeakReference
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class PlayerCharacterRepository internal constructor(private val server: Server) : CachingRepository<SimplePlayerCharacter> {
	private val cache = HashMap<ULong, WeakReference<SimplePlayerCharacter>>()
	private val dirty = HashSet<SimplePlayerCharacter>()

	override fun save(v: SimplePlayerCharacter) {
		saveRaw(v)
		dirty.remove(v)
	}

	private fun saveRaw(c: SimplePlayerCharacter) {
		val stmnt = fablesDatabase.prepareStatement("UPDATE fables_characters SET " +
				"name = ?, " +
				"age = ?, " +
				"description = ?, " +
				"money = ?, " +
				"location_x = ?, " +
				"location_y = ?, " +
				"location_z = ?, " +
				"location_yaw = ?, " +
				"location_pitch = ? " +
				"WHERE id = ?")
		stmnt.setString(1, c.name)
		stmnt.setInt(2, c.age.toInt())
		stmnt.setString(3, c.description)
		stmnt.setLong(4, c.money.toLong())
		stmnt.setDouble(5, c.location.x)
		stmnt.setDouble(6, c.location.y)
		stmnt.setDouble(7, c.location.z)
		stmnt.setFloat(8, c.location.yaw)
		stmnt.setFloat(9, c.location.pitch)
		stmnt.setLong(10, c.id.toLong())
		stmnt.executeUpdate()
	}

	fun create(name: String,
			   age: UInt,
			   description: String,
			   gender: Gender,
			   race: Race,
			   stats: CharacterStats,
			   location: Location,
			   money: ULong,
			   player: OfflinePlayer): SimplePlayerCharacter {
		val stmnt = fablesDatabase.prepareStatement("INSERT INTO fables_characters " +
				"(player, name, description, age, race, gender, money, " +
				"location_x, location_y, location_z, location_yaw, location_pitch, " +
				"stat_strength, stat_defense, stat_agility, stat_intelligence) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
		stmnt.setObject(1, player.uniqueId)
		stmnt.setString(2, name)
		stmnt.setString(3, description)
		stmnt.setInt(4, age.toInt())
		stmnt.setString(5, race.name)
		stmnt.setInt(6, gender.ordinal)
		stmnt.setLong(7, money.toLong())
		stmnt.setDouble(8, location.x)
		stmnt.setDouble(9, location.y)
		stmnt.setDouble(10, location.z)
		stmnt.setFloat(11, location.yaw)
		stmnt.setFloat(12, location.pitch)
		stmnt.setInt(13, stats.strength.toInt())
		stmnt.setInt(14, stats.defense.toInt())
		stmnt.setInt(15, stats.agility.toInt())
		stmnt.setInt(16, stats.intelligence.toInt())
		stmnt.executeUpdate()
		stmnt.generatedKeys.next()
		val id = stmnt.generatedKeys.getLong("id").toULong()
		return SimplePlayerCharacter(this,
				id, name, age, description, gender, race,
				stats, location, money, player)
	}

	fun fromId(id: ULong): SimplePlayerCharacter {
		return fromCache(id) ?: run {
			val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_characters WHERE id = ?")
			stmnt.setInt(1, id.toInt())
			val result = stmnt.executeQuery()
			if (!result.next()) throw Exception("Character not found in database")
			val char = fromRow(result)
			cache[id] = WeakReference(char)
			char
		}
	}

	fun allFromPlayer(p: OfflinePlayer): List<SimplePlayerCharacter> {
		val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_characters WHERE player = ?")
		stmnt.setObject(1, p.uniqueId)
		val result = stmnt.executeQuery()
		val all = ArrayList<SimplePlayerCharacter>()
		while(result.next()) all.add(fromRowOrCache(result))
		return all
	}

	override fun destroy(v: SimplePlayerCharacter) {
		cache.remove(v.id)
		val stmnt = fablesDatabase.prepareStatement("DELETE FROM fables_characters WHERE id = ?")
		stmnt.setLong(1, v.id.toLong())
		stmnt.executeUpdate()
	}

	override fun markDirty(v: SimplePlayerCharacter) {
		dirty.add(v)
	}

	override fun saveAllDirty() {
		dirty.forEach { saveRaw(it) }
		dirty.clear()
	}

	private fun fromRow(result: ResultSet): SimplePlayerCharacter {
		val id = result.getLong("id").toULong()
		val name = result.getString("name")
		val age = result.getInt("age").toUInt()
		val description = result.getString("description")
		val gender = if (result.getInt("gender") == 1) Gender.MALE else Gender.FEMALE
		val race = Race.valueOf(result.getString("race").uppercase())
		val money = result.getLong("money").toULong()
		val locX = result.getFloat("location_x").toDouble()
		val locY = result.getFloat("location_y").toDouble()
		val locZ = result.getFloat("location_z").toDouble()
		val locPitch = result.getFloat("location_pitch")
		val locYaw = result.getFloat("location_yaw")
		val statStrength = result.getInt("stat_strength").toUInt()
		val statDefense = result.getInt("stat_defense").toUInt()
		val statAgility = result.getInt("stat_agility").toUInt()
		val statIntelligence = result.getInt("stat_intelligence").toUInt()
		val playerUuid = result.getObject("player") as UUID
		return SimplePlayerCharacter(this,
				id, name, age, description, gender, race,
				CharacterStats(statStrength, statDefense, statAgility, statIntelligence),
				Location(server.getWorld("Eden"), locX, locY, locZ, locYaw, locPitch), money,
				server.getOfflinePlayer(playerUuid))
	}

	private fun fromCache(id: ULong): SimplePlayerCharacter? {
		val maybe = cache[id]?.get()
		if (maybe == null) cache.remove(id)
		return maybe
	}

	private fun fromRowOrCache(result: ResultSet): SimplePlayerCharacter {
		val id = result.getLong("id").toULong()
		val maybe = fromCache(id)

		return if (maybe != null) {
			maybe
		} else {
			val surely = fromRow(result)
			cache[id] = WeakReference(surely)
			surely
		}
	}
}
