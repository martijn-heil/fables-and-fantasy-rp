package com.fablesfantasyrp.plugin.staffplayerinstances.data

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import org.bukkit.Server
import java.sql.ResultSet
import javax.sql.DataSource

class H2StaffPlayerInstanceRepository(private val server: Server,
									  private val dataSource: DataSource,
									  private val playerInstanceRepository: PlayerInstanceRepository)
	: StaffPlayerInstanceRepository {
	private val TABLE_NAME = "FABLES_STAFFPLAYERINSTANCES.STAFF_PLAYER_INSTANCES"

	override fun create(v: PlayerInstance): PlayerInstance {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (player_instance) VALUES (?)")
			stmnt.setInt(1, v.id)
			stmnt.executeUpdate()
			v
		}
	}

	override fun update(v: PlayerInstance) {
		throw NotImplementedError()
	}

	override fun all(): Collection<PlayerInstance> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<PlayerInstance>()
			while (result.next()) {
				val parsed = fromRow(result)
				all.add(parsed)
			}
			all
		}
	}

	override fun contains(v: PlayerInstance): Boolean {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE player_instance = ?")
			stmnt.setInt(1, v.id)
			stmnt.executeQuery().next()
		}
	}

	override fun containsAny(v: Collection<PlayerInstance>): Boolean {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE player_instance IN " +
					"(${v.joinToString(", ") { it.id.toString() }})")
			stmnt.executeQuery().next()
		}
	}

	override fun destroy(v: PlayerInstance) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE player_instance = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): PlayerInstance
		= playerInstanceRepository.forId(row.getInt("player_instance"))!!
}
