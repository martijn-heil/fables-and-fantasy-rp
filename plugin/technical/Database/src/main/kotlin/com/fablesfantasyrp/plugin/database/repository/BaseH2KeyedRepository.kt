package com.fablesfantasyrp.plugin.database.repository

import java.sql.ResultSet
import javax.sql.DataSource

abstract class BaseH2KeyedRepository<K, T: Identifiable<K>>(private val keyClass: Class<K>, private val dataSource: DataSource)
	: MutableRepository<T>, KeyedRepository<K, T>, HasDirtyMarker<T> {
	override var dirtyMarker: DirtyMarker<T>? = null
	protected abstract val TABLE_NAME: String

	override fun all(): Collection<T> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<T>()
			while (result.next()) {
				val parsed = fromRow(result)
				all.add(parsed)
			}
			all
		}
	}

	override fun destroy(v: T) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun forId(id: K): T? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun allIds(): Collection<K> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<K>()
			while (result.next()) {
				when (keyClass) {
					Int::class.java -> all.add(result.getInt("id") as K)
					Long::class.java -> all.add(result.getLong("id") as K)
					else -> all.add(result.getObject("id", keyClass))
				}
			}
			all
		}
	}

	protected abstract fun fromRow(row: ResultSet): T
}
