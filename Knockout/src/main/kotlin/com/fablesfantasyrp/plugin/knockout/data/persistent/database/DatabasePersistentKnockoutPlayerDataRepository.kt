package com.fablesfantasyrp.plugin.knockout.data.persistent.database

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

	override fun create(v: PersistentKnockoutPlayerData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(id, knocked_out_at, knockout_cause, damager) " +
					"VALUES (?, ?, ?, ?)")
			stmnt.setObject(1, v.id)
			val knockedOutAt = v.knockedOutAt
			val knockoutCause = v.knockoutCause
			val damager = v.knockoutDamager
			if (knockedOutAt != null) stmnt.setTimestamp(2, Timestamp.from(knockedOutAt))
			if (knockoutCause != null) stmnt.setString(3, knockoutCause.name)
			if (damager != null) stmnt.setObject(4, damager.uniqueId)
			stmnt.executeUpdate()
		}
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
					"knocked_out_at = ?, " +
					"knockout_cause = ?, " +
					"damager = ? " +
					"WHERE id = ?")
			val knockedOutAt = v.knockedOutAt
			val knockoutCause = v.knockoutCause
			val damager = v.knockoutDamager
			if (knockedOutAt != null) stmnt.setTimestamp(1, Timestamp.from(knockedOutAt))
			if (knockoutCause != null) stmnt.setString(2, knockoutCause.name)
			if (damager != null) stmnt.setObject(3, damager.uniqueId)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): DatabaseKnockoutPlayerData {
		val id = checkNotNull(row.getObject("id", UUID::class.java))

		val knockedOutAt = row.getTimestamp("knocked_out_at").toInstant()
		val knockoutCause = EntityDamageEvent.DamageCause.valueOf(row.getString("knockout_cause"))
		val damager = row.getObject("damager", UUID::class.java)

		return DatabaseKnockoutPlayerData(id,
				isKnockedOut = knockedOutAt != null,
				knockedOutAt = knockedOutAt,
				knockoutDamager = server.getEntity(damager),
				knockoutCause = knockoutCause)
	}
}
