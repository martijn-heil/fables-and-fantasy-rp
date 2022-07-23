package com.fablesfantasyrp.plugin.knockout.data.persistent.database

import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerData
import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerDataRepository
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.h2.jdbc.JdbcSQLDataException
import java.io.Serializable
import java.sql.ResultSet
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
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (id, channel, disabled_channels, reception_indicator_enabled) " +
					"VALUES (?, ?, ?, ?)")
			stmnt.setObject(1, v.id)
			if (v.channel is Serializable) stmnt.setObject(2, v.channel)
			stmnt.setArray(3, connection.createArrayOf("JAVA_OBJECT", v.disabledChannels.filter { it is Serializable }.toTypedArray()))
			stmnt.setBoolean(4, v.isReceptionIndicatorEnabled)
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
					"channel = ?, " +
					"disabled_channels = ?, " +
					"chat_style = ?, " +
					"reception_indicator_enabled = ? " +
					"WHERE id = ?")
			if (v.channel is Serializable) stmnt.setObject(1, v.channel)
			stmnt.setArray(2, connection.createArrayOf("JAVA_OBJECT", v.disabledChannels.filter { it is Serializable }.toTypedArray()))
			stmnt.setString(3, v.chatStyle?.let { GsonComponentSerializer.gson().serialize(Component.text().style(it).build()) })
			stmnt.setBoolean(4, v.isReceptionIndicatorEnabled)
			stmnt.setObject(5, v.id)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): DatabaseKnockoutPlayerData {
		val id = checkNotNull(row.getObject("id", UUID::class.java))
		val channel = try {
			row.getObject("channel") as ChatChannel
		} catch (ex: JdbcSQLDataException) {
			ChatOutOfCharacter
		}

		val disabledChannels = try {
			row.getArray("disabled_channels")?.array
					?.let { it as Array<Any> }
					?.let { it.mapNotNull { it as? ToggleableChatChannel } }?.toSet() ?: emptySet()
		} catch (ex: JdbcSQLDataException) {
			emptySet()
		}
		checkNotNull(disabledChannels)

		val chatStyle = row.getString("chat_style")?.let { GsonComponentSerializer.gson().deserialize(it).style() }
		val isChatReceptionIndicatorEnabled = row.getBoolean("reception_indicator_enabled")
		return DatabaseKnockoutPlayerData(id, channel, chatStyle, disabledChannels, isChatReceptionIndicatorEnabled)
	}
}
