package com.fablesfantasyrp.plugin.chat.data.persistent

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.ChatOutOfCharacter
import com.fablesfantasyrp.plugin.chat.channel.ToggleableChatChannel
import com.fablesfantasyrp.plugin.chat.data.ChatPlayerRepository
import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayer
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import org.h2.jdbc.JdbcSQLDataException
import java.io.Serializable
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class H2ChatPlayerRepository(private val plugin: Plugin, private val dataSource: DataSource) :
		ChatPlayerRepository, HasDirtyMarker<ChatPlayer> {
	val TABLE_NAME = "\"fables_chat\".CHAT"
	private val server = plugin.server

	override var dirtyMarker: DirtyMarker<ChatPlayer>? = null

	override fun all(): Collection<ChatPlayer> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<ChatPlayer>()
			while (result.next()) all.add(fromRow(result))
			all
		}
	}

	override fun destroy(v: ChatPlayer): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun create(v: ChatPlayer): ChatPlayer = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (id, channel, disabled_channels, reception_indicator_enabled) " +
					"VALUES (?, ?, ?, ?)")
			stmnt.setObject(1, v.id)
			stmnt.setObject(2, v.channel as? Serializable)
			stmnt.setArray(3, connection.createArrayOf("JAVA_OBJECT", v.disabledChannels.filter { it is Serializable }.toTypedArray()))
			stmnt.setBoolean(4, v.isReceptionIndicatorEnabled)
			stmnt.executeUpdate()
		}
		v
	}

	override fun forOfflinePlayer(offlinePlayer: OfflinePlayer): ChatPlayer = warnBlockingIO(plugin) {
		check(offlinePlayer.hasPlayedBefore())
		forId(offlinePlayer.uniqueId)!!
	}

	override fun forId(id: UUID): ChatPlayer? = warnBlockingIO(plugin) {
		var result: ChatPlayer?
		while (true) {
			result = this.forIdMaybe(id)
			val offlinePlayer = server.getOfflinePlayer(id)
			if (result == null && (offlinePlayer.isOnline || offlinePlayer.hasPlayedBefore())) {
				this.create(ChatPlayer(id))
				continue
			}
			break
		}
		result
	}

	private fun forIdMaybe(id: UUID): ChatPlayer? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) { return@use null }
			fromRow(result)
		}
	}

	override fun allIds(): Collection<UUID> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT id FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<UUID>()
			while (result.next()) all.add(result.getObject("id", UUID::class.java))
			all
		}
	}

	override fun update(v: ChatPlayer): Unit = warnBlockingIO(plugin) {
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

	override fun createOrUpdate(v: ChatPlayer): ChatPlayer {
		throw NotImplementedError()
	}

	private fun fromRow(row: ResultSet): ChatPlayer {
		val id = checkNotNull(row.getObject("id", UUID::class.java))
		val channel = try {
			row.getObject("channel") as? ChatChannel ?: ChatOutOfCharacter
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
		return ChatPlayer(
				id = id,
				channel = channel,
				chatStyle = chatStyle,
				disabledChannels = disabledChannels,
				isChatSpyEnabled = isChatSpyEnabled,
				chatSpyExcludeChannels = chatSpyExcludeChannels,
				isReceptionIndicatorEnabled = isReceptionIndicatorEnabled,
				dirtyMarker = dirtyMarker)
	}
}
