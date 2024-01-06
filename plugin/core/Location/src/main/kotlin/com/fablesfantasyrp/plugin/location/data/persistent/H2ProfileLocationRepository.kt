package com.fablesfantasyrp.plugin.location.data.persistent

import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.location.data.entity.ProfileLocation
import com.fablesfantasyrp.plugin.location.data.entity.ProfileLocationRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class H2ProfileLocationRepository(private val dataSource: DataSource,
								  private val plugin: Plugin,
								  private val profileManager: ProfileManager,
								  private val defaultLocation: Location)
	: ProfileLocationRepository, HasDirtyMarker<ProfileLocation> {
	override var dirtyMarker: DirtyMarker<ProfileLocation>? = null
	private val TABLE_NAME = "FABLES_LOCATION.LOCATION"
	private val server = plugin.server

	override fun forOwner(profile: Profile): ProfileLocation = warnBlockingIO(plugin) {
		check(!profile.isDestroyed)

		val player = profileManager.getCurrentForProfile(profile)

		val location = this.forId(profile.id) ?: run {
			this.create(ProfileLocation(profile.id, defaultLocation))
		}

		if (player != null && player.isOnline) {
			location.player = player
		}
		location
	}

	override fun all(): Collection<ProfileLocation> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<ProfileLocation>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: ProfileLocation) = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: ProfileLocation): ProfileLocation = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(" +
					"id, " +
					"location_x, " +
					"location_y, " +
					"location_z, " +
					"location_pitch, " +
					"location_yaw, " +
					"location_world" +
					") " +
					"VALUES (?, ?, ?, ?, ?, ?, ?)")
			val location = v.location
			stmnt.setInt(1, v.id)
			stmnt.setDouble(2, location.x)
			stmnt.setDouble(3, location.y)
			stmnt.setDouble(4, location.z)
			stmnt.setFloat(5, location.pitch)
			stmnt.setFloat(6, location.yaw)
			stmnt.setObject(7, location.world.uid)
			stmnt.executeUpdate()
			val obj = ProfileLocation(
					id = v.id,
					location,
					dirtyMarker = dirtyMarker
			)
			obj.player = v.player
			obj
		}
	}

	override fun update(v: ProfileLocation): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"location_x = ?, " +
					"location_y = ?, " +
					"location_z = ?, " +
					"location_pitch = ?, " +
					"location_yaw = ?, " +
					"location_world = ? " +
				"WHERE id = ?")
			val location = v.location
			stmnt.setDouble(1, location.x)
			stmnt.setDouble(2, location.y)
			stmnt.setDouble(3, location.z)
			stmnt.setFloat(4, location.pitch)
			stmnt.setFloat(5, location.yaw)
			stmnt.setObject(6, (location.world ?: defaultLocation.world).uid)
			stmnt.setInt(7, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun createOrUpdate(v: ProfileLocation): ProfileLocation {
		throw NotImplementedError()
	}

	override fun forId(id: Int): ProfileLocation? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return@use null
			fromRow(result)
		}
	}

	override fun allIds(): Collection<Int> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Int>()
			while (result.next()) all.add(result.getInt("id"))
			all
		}
	}

	private fun fromRow(row: ResultSet): ProfileLocation {
		val id = row.getInt("id")
		val x = row.getDouble("location_x")
		val y = row.getDouble("location_y")
		val z = row.getDouble("location_z")
		val pitch = row.getFloat("location_pitch")
		val yaw = row.getFloat("location_yaw")
		val worldUniqueId = row.getObject("location_world", UUID::class.java)
		val world = server.getWorld(worldUniqueId) ?: throw IllegalStateException()

		val location = Location(world, x, y, z, yaw, pitch)

		return ProfileLocation(
				id = id,
				location = location,
				dirtyMarker = dirtyMarker
		)
	}
}
