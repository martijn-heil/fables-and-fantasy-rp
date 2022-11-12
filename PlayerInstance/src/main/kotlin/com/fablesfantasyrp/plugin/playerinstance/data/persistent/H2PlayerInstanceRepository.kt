package com.fablesfantasyrp.plugin.playerinstance.data.persistent

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceInventory
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import javax.sql.DataSource

class H2PlayerInstanceRepository(private val server: Server,
								 private val dataSource: DataSource) : PlayerInstanceRepository, HasDirtyMarker<PlayerInstanceInventory> {
	override var dirtyMarker: DirtyMarker<PlayerInstanceInventory>? = null
	private val TABLE_NAME = "FABLES_PLAYERINSTANCE.PLAYERINSTANCE"

	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<PlayerInstanceInventory> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE owner = ?")
			stmnt.setObject(1, offlinePlayer.uniqueId)
			val result = stmnt.executeQuery()
			val results = ArrayList<PlayerInstanceInventory>()
			while (result.next()) {
				results.add(fromRow(result))
			}
			results
		}
	}

	override fun all(): Collection<PlayerInstanceInventory> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<PlayerInstanceInventory>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: PlayerInstanceInventory) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
			v.isDestroyed = true
		}
	}

	override fun create(v: PlayerInstanceInventory): PlayerInstanceInventory {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(owner) " +
					"VALUES (?)", Statement.RETURN_GENERATED_KEYS)
			stmnt.setObject(1, v.owner.uniqueId)
			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			return PlayerInstanceInventory(
					id = id,
					owner = v.owner,
					dirtyMarker = dirtyMarker
			)
		}
	}

	override fun update(v: PlayerInstanceInventory) {
		throw NotImplementedError()
	}

	override fun forId(id: Int): PlayerInstanceInventory? {
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

	private fun fromRow(row: ResultSet): PlayerInstanceInventory {
		val id = row.getInt("id")
		val owner = server.getOfflinePlayer(row.getObject(2, UUID::class.java))

		return PlayerInstanceInventory(
				id = id,
				owner = owner,
				dirtyMarker = dirtyMarker
		)
	}
}
