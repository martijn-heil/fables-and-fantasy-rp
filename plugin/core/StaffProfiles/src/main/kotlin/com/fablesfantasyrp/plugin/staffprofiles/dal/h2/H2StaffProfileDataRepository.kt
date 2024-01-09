package com.fablesfantasyrp.plugin.staffprofiles.dal.h2

import com.fablesfantasyrp.plugin.database.setCollection
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.staffprofiles.dal.repository.StaffProfileDataRepository
import org.bukkit.plugin.Plugin
import org.h2.api.H2Type
import java.sql.ResultSet
import javax.sql.DataSource

class H2StaffProfileDataRepository(private val plugin: Plugin,
								   private val dataSource: DataSource)
	: StaffProfileDataRepository {
	private val TABLE_NAME = "FABLES_STAFFPROFILES.STAFF_PROFILES"

	override fun create(v: Int): Int = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (profile) VALUES (?)")
			stmnt.setInt(1, v)
			stmnt.executeUpdate()
			v
		}
	}

	override fun update(v: Int) { throw NotImplementedError() }
	override fun createOrUpdate(v: Int): Int = create(v)

	override fun all(): Collection<Int> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Int>()
			while (result.next()) {
				val parsed = fromRow(result)
				all.add(parsed)
			}
			all
		}
	}

	override fun contains(v: Int): Boolean = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile = ?").apply {
				this.setInt(1, v)
			}.executeQuery().next()
		}
	}

	override fun containsAny(v: Collection<Int>): Boolean = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile IN ?").apply {
				this.setCollection(1, H2Type.INTEGER, v)
			}.executeQuery().next()
		}
	}

	override fun destroy(v: Int): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE profile = ?")
			stmnt.setInt(1, v)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): Int = row.getInt("profile")
}
