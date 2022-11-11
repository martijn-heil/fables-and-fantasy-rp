package com.fablesfantasyrp.plugin.characters.data.persistent.denizen

import com.fablesfantasyrp.plugin.characters.data.entity.PlayerCharacter
import com.fablesfantasyrp.plugin.characters.data.entity.PlayerCharacterRepository
import com.fablesfantasyrp.plugin.database.repository.CachingRepository
import org.bukkit.plugin.Plugin

//class H2PlayerCharacterRepository internal constructor(private val plugin: Plugin)
//	: PlayerCharacterRepository, CachingRepository<PlayerCharacter> {
//	private val server = plugin.server
//
//	private fun saveRaw(c: PlayerCharacter) {
//		val stmnt = fablesDatabase.prepareStatement("UPDATE fables_characters SET " +
//				"name = ?, " +
//				"age = ?, " +
//				"description = ?, " +
//				"WHERE id = ?")
//		// TODO save stats
//		stmnt.setString(1, c.name)
//		stmnt.setInt(2, c.age.toInt())
//		stmnt.setString(3, c.description)
//		stmnt.setLong(13, c.id.toLong())
//		stmnt.executeUpdate()
//	}
//
//	override fun create(v: PlayerCharacter): PlayerCharacter {
//		val stmnt = fablesDatabase.prepareStatement("INSERT INTO fables_characters " +
//				"(player_instance, name, description, age, race, gender, created_at, " +
//				"stat_strength, stat_defense, stat_agility, stat_intelligence) " +
//				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
//		stmnt.setLong(1, playerInstance.id.toLong())
//		stmnt.setString(2, name)
//		stmnt.setString(3, description)
//		stmnt.setInt(4, age.toInt())
//		stmnt.setString(5, race.name)
//		stmnt.setInt(6, gender.ordinal)
//		stmnt.setTimestamp(7, if (createdAt != null) Timestamp.from(createdAt) else null)
//		stmnt.setInt(8, stats.strength.toInt())
//		stmnt.setInt(9, stats.defense.toInt())
//		stmnt.setInt(10, stats.agility.toInt())
//		stmnt.setInt(11, stats.intelligence.toInt())
//		stmnt.executeUpdate()
//		stmnt.generatedKeys.next()
//		val id = stmnt.generatedKeys.getLong("id").toULong()
//		val playerCharacter = DatabasePlayerCharacter(this, id, name, age, description, gender, race, stats, playerInstance)
//		cache[id] = WeakReference(playerCharacter)
//		nameMap[name] = id
//		return playerCharacter
//	}
//
//	override fun forId(id: ULong): PlayerCharacter? {
//		return fromCache(id) ?: run {
//			val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_characters WHERE id = ?")
//			stmnt.setInt(1, id.toInt())
//			val result = stmnt.executeQuery()
//			if (!result.next()) return null
//			val char = fromRow(result)
//			cache[id] = WeakReference(char)
//			char
//		}
//	}
//
//	// TODO optimize
//	override fun forPlayerInstance(playerInstance: PlayerInstance): PlayerCharacter? {
//		return cache.values.asSequence().mapNotNull { it.get() }.find { it.playerInstance == playerInstance }
//	}
//
//	override fun all(): Collection<PlayerCharacter> {
//		val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_characters")
//		val result = stmnt.executeQuery()
//		val all = ArrayList<PlayerCharacter>()
//		while(result.next()) all.add(fromRowOrCache(result))
//		return all
//	}
//
//	override fun allNames(): Set<String> = nameMap.keys.toSet()
//	override fun forName(name: String) = nameMap[name]?.let { this.forId(it) }
//
//	// TODO cache
//	override fun allForPerson(person: FablesPerson): List<PlayerCharacter> {
//		this.saveAllDirty()
//		val stmnt = fablesDatabase.prepareStatement(
//				"SELECT fables_characters.* FROM fables_characters " +
//						"INNER JOIN fables_player_instances " +
//						"ON fables_characters.player_instance=fables_player_instances.id " +
//						"WHERE fables_player_instances.owner = ?"
//		)
//		stmnt.setLong(1, person.id.toLong())
//		val result = stmnt.executeQuery()
//		val all = ArrayList<PlayerCharacter>()
//		while (result.next()) all.add(fromRowOrCache(result))
//		return all
//	}
//
//	override fun destroy(v: PlayerCharacter) {
//		cache.remove(v.id)
//		dirty.remove(v)
//		nameMap.remove(v.name)
//		val stmnt = fablesDatabase.prepareStatement("DELETE FROM fables_characters WHERE id = ?")
//		stmnt.setLong(1, v.id.toLong())
//		stmnt.executeUpdate()
//	}
//
//	override fun markDirty(v: PlayerCharacter) {
//		dirty.add(v)
//	}
//
//	override fun saveAllDirty() {
//		dirty.forEach { saveRaw(it) }
//		dirty.clear()
//	}
//
//	private fun fromRow(result: ResultSet): DatabasePlayerCharacter {
//		val id = result.getLong("id").toULong()
//		val name = result.getString("name")
//		val age = result.getInt("age").toUInt()
//		val description = result.getString("description")
//		val gender = if (result.getInt("gender") == 1) Gender.MALE else Gender.FEMALE
//		val race = Race.valueOf(result.getString("race").uppercase())
//		val statStrength = result.getInt("stat_strength").toUInt()
//		val statDefense = result.getInt("stat_defense").toUInt()
//		val statAgility = result.getInt("stat_agility").toUInt()
//		val statIntelligence = result.getInt("stat_intelligence").toUInt()
//		val playerInstanceId = result.getLong("player_instance").toULong()
//
//		return DatabasePlayerCharacter(this,
//				id, name, age, description, gender, race,
//				CharacterStats(statStrength, statDefense, statAgility, statIntelligence), PlayerInstance.forId(playerInstanceId)!!)
//	}
//
//	private fun fromCache(id: ULong): PlayerCharacter? {
//		val maybe = cache[id]?.get()
//		if (maybe == null) cache.remove(id)
//		return maybe
//	}
//
//	private fun fromRowOrCache(result: ResultSet): PlayerCharacter {
//		val id = result.getLong("id").toULong()
//		val maybe = fromCache(id)
//
//		return if (maybe != null) {
//			maybe
//		} else {
//			val surely = fromRow(result)
//			cache[id] = WeakReference(surely)
//			surely
//		}
//	}
//
//	fun markStronglyCached(character: DatabasePlayerCharacter) {
//		strongCache.add(character)
//	}
//
//	fun unmarkStronglyCached(character: DatabasePlayerCharacter) {
//		strongCache.remove(character)
//	}
//}
