package com.fablesfantasyrp.plugin.knockout.data.persistent.database

import com.fablesfantasyrp.plugin.knockout.data.KnockoutState
import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerData
import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerDataRepository
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.event.entity.EntityDamageEvent
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*
import javax.sql.DataSource

class DatabasePersistentKnockoutPlayerDataRepository(private val server: Server, private val dataSource: DataSource) : PersistentKnockoutPlayerDataRepository {
	val TABLE_NAME = "\"fables_knockout\".KNOCKOUT"

	override fun all(): Collection<PersistentKnockoutPlayerData> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<DatabaseKnockoutPlayerData>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: PersistentKnockoutPlayerData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun create(v: PersistentKnockoutPlayerData): PersistentKnockoutPlayerData {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, state, knocked_out_at, knockout_cause, damager) " +
					"VALUES (?, ?, ?, ?, ?)")
			stmnt.setObject(1, v.id)
			val knockedOutAt = v.knockedOutAt
			val knockoutCause = v.knockoutCause
			val damager = v.knockoutDamager
			stmnt.setString(2, v.state?.name)
			stmnt.setTimestamp(3, knockedOutAt?.let { Timestamp.from(it) })
			stmnt.setString(4, knockoutCause?.name)
			stmnt.setObject(5, damager?.uniqueId)
			stmnt.executeUpdate()
		}
		return v
	}

	override fun forOfflinePlayer(offlinePlayer: OfflinePlayer): PersistentKnockoutPlayerData {
		check(offlinePlayer.hasPlayedBefore())
		return forId(offlinePlayer.uniqueId)!!
	}

	override fun forId(id: UUID): PersistentKnockoutPlayerData? {
		var result: PersistentKnockoutPlayerData?
		while (true) {
			result = this.forIdMaybe(id)
			val offlinePlayer = server.getOfflinePlayer(id)
			if (result == null && (offlinePlayer.isOnline || offlinePlayer.hasPlayedBefore())) {
				this.create(DatabaseKnockoutPlayerData(id))
				continue
			}
			break
		}
		return result
	}

	private fun forIdMaybe(id: UUID): PersistentKnockoutPlayerData? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) { return null }
			fromRow(result)
		}
	}

	override fun allIds(): Collection<UUID> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<UUID>()
			while (result.next()) all.add(result.getObject("id", UUID::class.java))
			all
		}
	}

	override fun update(v: PersistentKnockoutPlayerData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"state = ?, " +
					"knocked_out_at = ?, " +
					"knockout_cause = ?, " +
					"damager = ? " +
					"WHERE id = ?")
			val knockedOutAt = v.knockedOutAt
			val knockoutCause = v.knockoutCause
			val damager = v.knockoutDamager
			stmnt.setString(1, v.state?.name)
			stmnt.setTimestamp(2, knockedOutAt?.let { Timestamp.from(it) })
			stmnt.setString(3, knockoutCause?.name)
			stmnt.setObject(4, damager?.uniqueId)
			stmnt.setObject(5, v.id)
			stmnt.executeUpdate()
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): DatabaseKnockoutPlayerData {
		val id = checkNotNull(row.getObject("id", UUID::class.java))

		val state = row.getString("state")?.let { KnockoutState.valueOf(it) }
		val knockedOutAt = row.getTimestamp("knocked_out_at")?.toInstant()
		val knockoutCause = row.getString("knockout_cause")?.let { EntityDamageEvent.DamageCause.valueOf(it) }
		val damager: UUID? = row.getObject("damager", UUID::class.java)

		return DatabaseKnockoutPlayerData(id,
				state = state,
				isKnockedOut = knockedOutAt != null,
				knockedOutAt = knockedOutAt,
				knockoutDamager = damager?.let { server.getEntity(it) },
				knockoutCause = knockoutCause)
	}
}
