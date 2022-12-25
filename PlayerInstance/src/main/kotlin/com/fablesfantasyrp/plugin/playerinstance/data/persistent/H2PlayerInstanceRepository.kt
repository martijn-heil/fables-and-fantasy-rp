package com.fablesfantasyrp.plugin.playerinstance.data.persistent

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import javax.sql.DataSource

class H2PlayerInstanceRepository(private val server: Server,
								 private val dataSource: DataSource) : PlayerInstanceRepository, HasDirtyMarker<PlayerInstance> {
	override var dirtyMarker: DirtyMarker<PlayerInstance>? = null
	private val TABLE_NAME = "FABLES_PLAYERINSTANCE.PLAYERINSTANCE"

	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<PlayerInstance> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE owner = ?")
			stmnt.setObject(1, offlinePlayer.uniqueId)
			val result = stmnt.executeQuery()
			val results = ArrayList<PlayerInstance>()
			while (result.next()) {
				results.add(fromRow(result))
			}
			results
		}
	}

	override fun all(): Collection<PlayerInstance> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<PlayerInstance>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: PlayerInstance) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: PlayerInstance): PlayerInstance {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(owner, description, active) " +
					"VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
			stmnt.setObject(1, v.owner.uniqueId)
			stmnt.setString(2, v.description)
			stmnt.setBoolean(3, v.isActive)
			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			return PlayerInstance(
					id = id,
					owner = v.owner,
					description = v.description,
					isActive = v.isActive,
					dirtyMarker = dirtyMarker
			)
		}
	}

	override fun update(v: PlayerInstance) {
		throw NotImplementedError()
	}

	override fun forId(id: Int): PlayerInstance? {
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

	private fun fromRow(row: ResultSet): PlayerInstance {
		val id = row.getInt("id")
		val owner = server.getOfflinePlayer(row.getObject("owner", UUID::class.java))
		val description = row.getString("description")
		val isAccessible = row.getBoolean("active")

		return PlayerInstance(
				id = id,
				owner = owner,
				description = description,
				isActive = isAccessible,
				dirtyMarker = dirtyMarker
		)
	}
}
