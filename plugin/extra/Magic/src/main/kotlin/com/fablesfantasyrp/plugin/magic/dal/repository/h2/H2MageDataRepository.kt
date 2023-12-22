package com.fablesfantasyrp.plugin.magic.dal.repository.h2

import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.dal.model.MageData
import com.fablesfantasyrp.plugin.magic.dal.repository.MageDataRepository
import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import java.sql.ResultSet
import javax.sql.DataSource

class H2MageDataRepository(private val dataSource: DataSource, private val spells: SpellDataRepository) : MageDataRepository,
	BaseH2KeyedRepository<Long, MageData>(Long::class.java, dataSource) {
	override val TABLE_NAME = "FABLES_MAGIC.MAGES"

	override fun create(v: MageData): MageData {
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

	override fun forCharacter(characterId: Int): MageData {
		return forId(characterId.toLong())!!
	}

	override fun forCharacterOrCreate(characterId: Int): MageData {
		return forIdMaybe(characterId.toLong()) ?: run {
			this.create(MageData(
				magicPath = MagicPath.AEROMANCY,
				magicLevel = 1,
				spells = emptyList())
			)
		}
	}

	private fun forIdMaybe(id: Long): MageData? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) { return null }
			fromRow(result)
		}
	}

	override fun update(v: MageData) {
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

	override fun fromRow(row: ResultSet): MageData {
		val id = row.getLong("id")

		val magicPath = row.getString("magic_path").let { MagicPath.valueOf(it) }
		val magicLevel = row.getInt("magic_level")
		val spells = (row.getArray("spells").array as Array<*>)
				.asSequence()
				.map { it as String }
				.mapNotNull { spells.forId(it) }

		val mage = MageData(
				id = id,
				magicPath = magicPath,
				magicLevel = magicLevel,
				spells = spells.toList(),
		)
		return mage
	}

	override fun createOrUpdate(v: MageData): MageData {
		throw NotImplementedError()
	}
}
