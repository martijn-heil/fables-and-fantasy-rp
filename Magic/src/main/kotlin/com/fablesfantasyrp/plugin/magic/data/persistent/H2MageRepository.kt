package com.fablesfantasyrp.plugin.magic.data.persistent

import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.data.entity.MageRepository
import com.fablesfantasyrp.plugin.magic.spellRepository
import org.bukkit.Server
import java.sql.ResultSet
import javax.sql.DataSource

class H2MageRepository(private val server: Server, private val dataSource: DataSource) : MageRepository, HasDirtyMarker<Mage> {
	val TABLE_NAME = "FABLES_MAGIC.MAGES"

	override var dirtyMarker: DirtyMarker<Mage>? = null

	override fun all(): Collection<Mage> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Mage>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: Mage) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun create(v: Mage): Mage {
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
		v.dirtyMarker = dirtyMarker
		return v
	}

	override fun forPlayerCharacter(c: CharacterData): Mage {
		return forId(c.id.toLong())!!
	}

	override fun forPlayerCharacterOrCreate(c: CharacterData): Mage {
		return forIdMaybe(c.id.toLong()) ?: run {
			this.create(Mage(0, MagicPath.AEROMANCY, 1, emptyList()))
		}
	}

	override fun forId(id: Long): Mage? {
		return this.forIdMaybe(id)
	}

	private fun forIdMaybe(id: Long): Mage? {
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

	override fun update(v: Mage) {
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

	private fun fromRow(row: ResultSet): Mage {
		val id = row.getLong("id")

		val magicPath = row.getString("magic_path").let { MagicPath.valueOf(it) }
		val magicLevel = row.getInt("magic_level")
		val spells = (row.getArray("spells").array as Array<*>)
				.asSequence()
				.map { it as String }
				.mapNotNull { spellRepository.forId(it) }

		val mage = Mage(
				id = id,
				magicPath = magicPath,
				magicLevel = magicLevel,
				spells = spells.toList(),
		)
		mage.dirtyMarker = dirtyMarker
		return mage
	}
}
