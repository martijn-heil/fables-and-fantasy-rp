package com.fablesfantasyrp.plugin.chat.data.persistent.database

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.ToggleableChatChannel
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerDataRepository
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.OfflinePlayer
import java.io.Serializable
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class DatabasePersistentChatPlayerDataRepository(private val dataSource: DataSource) : PersistentChatPlayerDataRepository {
	val TABLE_NAME = "chat"

	override fun all(): Collection<PersistentChatPlayerData> {
		val conn = dataSource.connection
		val stmnt = conn.prepareStatement("SELECT * FROM $TABLE_NAME")
		val result = stmnt.executeQuery()
		val all = ArrayList<DatabaseChatPlayerData>()
		while (result.next()) all.add(fromRow(result))
		conn.close()
		return all
	}

	override fun destroy(v: PersistentChatPlayerData) {
		val conn = dataSource.connection
		val stmnt = conn.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
		stmnt.setObject(1, v.id)
		stmnt.executeUpdate()
		conn.close()
	}

	override fun create(v: PersistentChatPlayerData) {
		val conn = dataSource.connection
		val stmnt = conn.prepareStatement("INSERT INTO $TABLE_NAME (id, channel, disabled_channels) " +
				"VALUES (?, ?, ?)")
		stmnt.setObject(1, v.id)
		if (v.channel is Serializable) stmnt.setObject(2, v.channel)
		stmnt.setArray(3, conn.createArrayOf("JAVA_OBJECT", v.disabledChannels.filter { it is Serializable }.toTypedArray()))
		conn.close()
	}

	override fun forOfflinePlayer(offlinePlayer: OfflinePlayer): PersistentChatPlayerData {
		val conn = dataSource.connection
		var result: PersistentChatPlayerData?
		while (true) {
			result = this.forId(offlinePlayer.uniqueId)
			if (result == null) { this.create(DatabaseChatPlayerData(offlinePlayer.uniqueId)); continue }
			break
		}
		conn.close()
		return result!!
	}

	override fun forId(id: UUID): PersistentChatPlayerData? {
		val conn = dataSource.connection
		val stmnt = conn.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
		stmnt.setObject(1, id)
		val result = stmnt.executeQuery()
		if (!result.next()) { return null }
		val obj = fromRow(result)
		conn.close()
		return obj
	}

	override fun allIds(): Collection<UUID> {
		val conn = dataSource.connection
		val stmnt = conn.prepareStatement("SELECT id FROM $TABLE_NAME")
		val result = stmnt.executeQuery()
		val all = ArrayList<UUID>()
		while (result.next()) all.add(result.getObject("id", UUID::class.java))
		conn.close()
		return all
	}

	override fun update(v: PersistentChatPlayerData) {
		val conn = dataSource.connection
		val stmnt = conn.prepareStatement("UPDATE $TABLE_NAME SET " +
				"channel = ?," +
				"disabled_channels = ? " +
				"chat_style = ?" +
				"WHERE id = ?")
		if (v.channel is Serializable) stmnt.setObject(1, v.channel)
		stmnt.setArray(2, conn.createArrayOf("JAVA_OBJECT", v.disabledChannels.filter { it is Serializable }.toTypedArray()))
		stmnt.setString(3, v.chatStyle?.let { GsonComponentSerializer.gson().serialize(Component.text().style(it).build()) })
		stmnt.setObject(4, v.id)
		stmnt.executeUpdate()
		conn.close()
	}

	private fun fromRow(row: ResultSet): DatabaseChatPlayerData {
		val id = row.getObject("id", UUID::class.java)
		val channel = row.getObject("channel") as ChatChannel
		val disabledChannels = row.getArray("disabled_channels")?.array
				?.let { it as Array<Any> }
				?.let { it.map { it as ToggleableChatChannel } }?.toSet() ?: emptySet()
		val chatStyle = row.getString("chat_style")?.let { GsonComponentSerializer.gson().deserialize(it).style() }
		return DatabaseChatPlayerData(id, channel, chatStyle, disabledChannels)
	}
}
