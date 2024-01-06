package com.fablesfantasyrp.plugin.staffprofiles.data

import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import javax.sql.DataSource

class H2StaffProfileRepository(private val plugin: Plugin,
							   private val dataSource: DataSource,
							   private val profileRepository: ProfileRepository)
	: StaffProfileRepository {
	private val TABLE_NAME = "FABLES_STAFFPROFILES.STAFF_PROFILES"

	override fun create(v: Profile): Profile = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (profile) VALUES (?)")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
			v
		}
	}

	override fun update(v: Profile) {
		throw NotImplementedError()
	}

	override fun createOrUpdate(v: Profile): Profile {
		throw NotImplementedError()
	}

	override fun all(): Collection<Profile> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Profile>()
			while (result.next()) {
				val parsed = fromRow(result)
				all.add(parsed)
			}
			all
		}
	}

	override fun contains(v: Profile): Boolean = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeQuery().next()
		}
	}

	override fun containsAny(v: Collection<Profile>): Boolean = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile IN " +
					"(${v.joinToString(", ") { it.id.toString() }})")
			stmnt.executeQuery().next()
		}
	}

	override fun destroy(v: Profile): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE profile = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): Profile
		= profileRepository.forId(row.getInt("profile"))!!
}
