package com.fablesfantasyrp.plugin.characters.database

import com.fablesfantasyrp.plugin.characters.CharacterStats
import com.fablesfantasyrp.plugin.characters.Gender
import com.fablesfantasyrp.plugin.characters.Race
import com.fablesfantasyrp.plugin.characters.playerCharacterRepository
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.repository.CachingRepository
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import java.lang.ref.WeakReference
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class DatabasePlayerCharacterRepository internal constructor(private val plugin: Plugin) : CachingRepository<DatabasePlayerCharacter> {
	private val cache = HashMap<ULong, WeakReference<DatabasePlayerCharacter>>()
	private val dirty = HashSet<DatabasePlayerCharacter>()
	private val server = plugin.server

	override fun save(v: DatabasePlayerCharacter) {
		saveRaw(v)
		dirty.remove(v)
	}

	private fun saveRaw(c: DatabasePlayerCharacter) {
		val stmnt = fablesDatabase.prepareStatement("UPDATE fables_characters SET " +
				"name = ?, " +
				"age = ?, " +
				"description = ?, " +
				"money = ?, " +
				"location_x = ?, " +
				"location_y = ?, " +
				"location_z = ?, " +
				"location_yaw = ?, " +
				"location_pitch = ?, " +
				"location_world = ?" +
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
		stmnt.setObject(10, c.location.world!!.uid)
		stmnt.setLong(11, c.id.toLong())
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
			   player: OfflinePlayer): DatabasePlayerCharacter {
		val stmnt = fablesDatabase.prepareStatement("INSERT INTO fables_characters " +
				"(player, name, description, age, race, gender, money, " +
				"location_x, location_y, location_z, location_yaw, location_pitch, location_world, " +
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
		stmnt.setObject(13, location.world!!.uid)
		stmnt.setInt(14, stats.strength.toInt())
		stmnt.setInt(15, stats.defense.toInt())
		stmnt.setInt(16, stats.agility.toInt())
		stmnt.setInt(17, stats.intelligence.toInt())
		stmnt.executeUpdate()
		stmnt.generatedKeys.next()
		val id = stmnt.generatedKeys.getLong("id").toULong()
		return DatabasePlayerCharacter(this,
				id, name, age, description, gender, race,
				stats, location, money, player)
	}

	fun forId(id: ULong): DatabasePlayerCharacter {
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

	fun all(): List<DatabasePlayerCharacter> {
		val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_characters")
		val result = stmnt.executeQuery()
		val all = ArrayList<DatabasePlayerCharacter>()
		while(result.next()) all.add(fromRowOrCache(result))
		return all
	}

	fun allForPlayer(p: OfflinePlayer): List<DatabasePlayerCharacter> {
		val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_characters WHERE player = ?")
		stmnt.setObject(1, p.uniqueId)
		val result = stmnt.executeQuery()
		val all = ArrayList<DatabasePlayerCharacter>()
		while(result.next()) all.add(fromRowOrCache(result))
		return all
	}

	override fun destroy(v: DatabasePlayerCharacter) {
		cache.remove(v.id)
		dirty.remove(v)
		val stmnt = fablesDatabase.prepareStatement("DELETE FROM fables_characters WHERE id = ?")
		stmnt.setLong(1, v.id.toLong())
		stmnt.executeUpdate()
	}

	override fun markDirty(v: DatabasePlayerCharacter) {
		dirty.add(v)
	}

	override fun saveAllDirty() {
		dirty.forEach { saveRaw(it) }
		dirty.clear()
	}

	private fun fromRow(result: ResultSet): DatabasePlayerCharacter {
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
		val locWorld = result.getObject("location_world") as UUID
		val statStrength = result.getInt("stat_strength").toUInt()
		val statDefense = result.getInt("stat_defense").toUInt()
		val statAgility = result.getInt("stat_agility").toUInt()
		val statIntelligence = result.getInt("stat_intelligence").toUInt()
		val playerUuid = result.getObject("player") as UUID
		return DatabasePlayerCharacter(this,
				id, name, age, description, gender, race,
				CharacterStats(statStrength, statDefense, statAgility, statIntelligence),
				Location(server.getWorld(locWorld), locX, locY, locZ, locYaw, locPitch), money,
				server.getOfflinePlayer(playerUuid))
	}

	private fun fromCache(id: ULong): DatabasePlayerCharacter? {
		val maybe = cache[id]?.get()
		if (maybe == null) cache.remove(id)
		return maybe
	}

	private fun fromRowOrCache(result: ResultSet): DatabasePlayerCharacter {
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

fun DatabasePlayerCharacter.Companion.forId(id: ULong) = playerCharacterRepository.forId(id)
fun DatabasePlayerCharacter.Companion.all() = playerCharacterRepository.all()
fun DatabasePlayerCharacter.Companion.allForPlayer(p: OfflinePlayer) = playerCharacterRepository.allForPlayer(p)
fun DatabasePlayerCharacter.save() = playerCharacterRepository.save(this)
fun DatabasePlayerCharacter.destroy() = playerCharacterRepository.destroy(this)
