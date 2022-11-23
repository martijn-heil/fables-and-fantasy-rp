package com.fablesfantasyrp.plugin.location.data.persistent

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.location.data.entity.PlayerInstanceLocation
import com.fablesfantasyrp.plugin.location.data.entity.PlayerInstanceLocationRepository
import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.Location
import org.bukkit.Server
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class H2PlayerInstanceLocationRepository(private val dataSource: DataSource,
										 private val server: Server,
										 private val playerInstanceManager: PlayerInstanceManager,
										 private val defaultLocation: Location)
	: PlayerInstanceLocationRepository, HasDirtyMarker<PlayerInstanceLocation> {
	override var dirtyMarker: DirtyMarker<PlayerInstanceLocation>? = null
	private val TABLE_NAME = "FABLES_LOCATION.LOCATION"

	override fun forOwner(playerInstance: PlayerInstance): PlayerInstanceLocation {
		check(!playerInstance.isDestroyed)

		val player = playerInstanceManager.getCurrentForPlayerInstance(playerInstance)

		val location = this.forId(playerInstance.id) ?: run {
			this.create(PlayerInstanceLocation(playerInstance.id, defaultLocation))
		}

		if (player != null && player.isOnline) {
			location.player = player
		}
		return location
	}

	override fun all(): Collection<PlayerInstanceLocation> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<PlayerInstanceLocation>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: PlayerInstanceLocation) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: PlayerInstanceLocation): PlayerInstanceLocation {
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
			val obj = PlayerInstanceLocation(
					id = v.id,
					location,
					dirtyMarker = dirtyMarker
			)
			obj.player = v.player
			return obj
		}
	}

	override fun update(v: PlayerInstanceLocation) {
		return dataSource.connection.use { connection ->
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
			stmnt.setObject(6, location.world.uid)
			stmnt.setInt(7, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun forId(id: Int): PlayerInstanceLocation? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun allIds(): Collection<Int> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Int>()
			while (result.next()) all.add(result.getInt("id"))
			all
		}
	}

	private fun fromRow(row: ResultSet): PlayerInstanceLocation {
		val id = row.getInt("id")
		val x = row.getDouble("location_x")
		val y = row.getDouble("location_y")
		val z = row.getDouble("location_z")
		val pitch = row.getFloat("location_pitch")
		val yaw = row.getFloat("location_yaw")
		val worldUniqueId = row.getObject("location_world", UUID::class.java)
		val world = server.getWorld(worldUniqueId) ?: throw IllegalStateException()

		val location = Location(world, x, y, z, yaw, pitch)

		return PlayerInstanceLocation(
				id = id,
				location = location,
				dirtyMarker = dirtyMarker
		)
	}
}
