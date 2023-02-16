package com.fablesfantasyrp.plugin.chat.data.persistent.database

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.ChatOutOfCharacter
import com.fablesfantasyrp.plugin.chat.channel.ToggleableChatChannel
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerDataRepository
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.h2.jdbc.JdbcSQLDataException
import java.io.Serializable
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class DatabasePersistentChatPlayerDataRepository(private val server: Server, private val dataSource: DataSource) : PersistentChatPlayerDataRepository {
	val TABLE_NAME = "\"fables_chat\".CHAT"

	override fun all(): Collection<PersistentChatPlayerData> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<DatabaseChatPlayerData>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: PersistentChatPlayerData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun create(v: PersistentChatPlayerData): PersistentChatPlayerData {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (id, channel, disabled_channels, reception_indicator_enabled) " +
					"VALUES (?, ?, ?, ?)")
			stmnt.setObject(1, v.id)
			stmnt.setObject(2, v.channel as? Serializable)
			stmnt.setArray(3, connection.createArrayOf("JAVA_OBJECT", v.disabledChannels.filter { it is Serializable }.toTypedArray()))
			stmnt.setBoolean(4, v.isReceptionIndicatorEnabled)
			stmnt.executeUpdate()
		}
		return v
	}

	override fun forOfflinePlayer(offlinePlayer: OfflinePlayer): PersistentChatPlayerData {
		check(offlinePlayer.hasPlayedBefore())
		return forId(offlinePlayer.uniqueId)!!
	}

	override fun forId(id: UUID): PersistentChatPlayerData? {
		var result: PersistentChatPlayerData?
		while (true) {
			result = this.forIdMaybe(id)
			val offlinePlayer = server.getOfflinePlayer(id)
			if (result == null && (offlinePlayer.isOnline || offlinePlayer.hasPlayedBefore())) {
				this.create(DatabaseChatPlayerData(id))
				continue
			}
			break
		}
		return result
	}

	private fun forIdMaybe(id: UUID): PersistentChatPlayerData? {
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

	override fun update(v: PersistentChatPlayerData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
					"channel = ?, " +
					"disabled_channels = ?, " +
					"chat_spy_exclude_channels = ?, " +
					"chat_spy_enabled = ?, " +
					"chat_style = ?, " +
					"reception_indicator_enabled = ? " +
					"WHERE id = ?")
			stmnt.setObject(1, v.channel as? Serializable)
			stmnt.setArray(2, connection.createArrayOf("JAVA_OBJECT", v.disabledChannels.filter { it is Serializable }.toTypedArray()))
			stmnt.setArray(3, connection.createArrayOf("JAVA_OBJECT", v.chatSpyExcludeChannels.filter { it is Serializable }.toTypedArray()))
			stmnt.setBoolean(4, v.isChatSpyEnabled)
			stmnt.setString(5, v.chatStyle?.let { GsonComponentSerializer.gson().serialize(Component.text().style(it).build()) })
			stmnt.setBoolean(6, v.isReceptionIndicatorEnabled)
			stmnt.setObject(7, v.id)
			stmnt.executeUpdate()
		}
	}

	private fun fromRow(row: ResultSet): DatabaseChatPlayerData {
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

		val chatSpyExcludeChannels = try {
			row.getArray("chat_spy_exclude_channels")?.array
					?.let { it as Array<Any> }
					?.let { it.mapNotNull { it as? ToggleableChatChannel } }?.toSet() ?: emptySet()
		} catch (ex: JdbcSQLDataException) {
			emptySet()
		}
		checkNotNull(chatSpyExcludeChannels)

		val chatStyle = row.getString("chat_style")?.let { GsonComponentSerializer.gson().deserialize(it).style() }
		val isReceptionIndicatorEnabled = row.getBoolean("reception_indicator_enabled")
		val isChatSpyEnabled = row.getBoolean("chat_spy_enabled")
		return DatabaseChatPlayerData(
				id = id,
				channel = channel,
				chatStyle = chatStyle,
				disabledChannels = disabledChannels,
				isChatSpyEnabled = isChatSpyEnabled,
				chatSpyExcludeChannels = chatSpyExcludeChannels,
				isReceptionIndicatorEnabled = isReceptionIndicatorEnabled)
	}
}
