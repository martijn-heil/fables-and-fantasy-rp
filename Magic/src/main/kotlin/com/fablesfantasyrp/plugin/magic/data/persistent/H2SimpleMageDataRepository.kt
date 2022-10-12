package com.fablesfantasyrp.plugin.magic.data.persistent

import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.data.SimpleMageData
import com.fablesfantasyrp.plugin.magic.data.SimpleMageDataRepository
import com.fablesfantasyrp.plugin.magic.spellRepository
import org.bukkit.Server
import java.sql.ResultSet
import javax.sql.DataSource

class H2SimpleMageDataRepository(private val server: Server, private val dataSource: DataSource) : SimpleMageDataRepository {
	val TABLE_NAME = "FABLES_MAGIC.MAGES"

	override fun all(): Collection<SimpleMageData> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<SimpleMageData>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: SimpleMageData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun create(v: SimpleMageData): SimpleMageData {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, magic_path, magic_level, spells) " +
					"VALUES (?, ?, ?, ?)")
			stmnt.setLong(1, v.id)
			stmnt.setString(2, v.magicPath.name)
			stmnt.setInt(3, v.magicLevel)
			stmnt.setArray(4, connection.createArrayOf("VARCHAR ARRAY", v.spells.toTypedArray()))
			stmnt.executeUpdate()
		}
		return v
	}

	override fun forPlayerCharacter(playerCharacter: PlayerCharacterData): SimpleMageData {
		return forId(playerCharacter.id.toLong())!!
	}

	override fun forId(id: Long): SimpleMageData? {
		return this.forIdMaybe(id)
	}

	private fun forIdMaybe(id: Long): SimpleMageData? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) { return null }
			fromRow(result)
		}
	}

	override fun allIds(): Collection<Long> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Long>()
			while (result.next()) all.add(result.getLong("id"))
			all
		}
	}

	override fun update(v: SimpleMageData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"magic_path = ?, " +
					"magic_level = ?, " +
					"spells = ? " +
					"WHERE id = ?")
			stmnt.setString(1, v.magicPath.name)
			stmnt.setArray(3, connection.createArrayOf("VARCHAR", v.spells.map { it.id }.toTypedArray()))
			stmnt.setInt(2, v.magicLevel)
			stmnt.setObject(4, v.id)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): SimpleMageData {
		val id = row.getLong("id")

		val magicPath = row.getString("magic_path").let { MagicPath.valueOf(it) }
		val magicLevel = row.getInt("magic_level")
		val spells = (row.getArray("spells").array as Array<*>)
				.asSequence()
				.map { it as String }
				.mapNotNull { spellRepository.forId(it) }

		return SimpleMageData(
				id = id,
				magicPath = magicPath,
				magicLevel = magicLevel,
				spells = spells.toList(),
		)
	}
}
