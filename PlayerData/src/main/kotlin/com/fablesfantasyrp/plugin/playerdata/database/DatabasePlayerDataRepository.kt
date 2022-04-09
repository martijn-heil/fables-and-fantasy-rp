package com.fablesfantasyrp.plugin.playerdata.database

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.repository.CachingRepository
import com.fablesfantasyrp.plugin.playerdata.databasePlayerRepository
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.lang.ref.WeakReference
import java.sql.ResultSet
import java.sql.Types
import java.util.*


class DatabasePlayerRepository internal constructor(private val plugin: Plugin) : CachingRepository<DatabasePlayerData> {
	private val cache = HashMap<UUID, WeakReference<DatabasePlayerData>>()
	private val strongCache = HashSet<DatabasePlayerData>()
	private val dirty = LinkedHashSet<DatabasePlayerData>()
	private val server = plugin.server

	init {
		server.pluginManager.registerEvents(object : Listener {
			@EventHandler(priority = MONITOR)
			fun onPlayerJoin(e: PlayerJoinEvent) {
				strongCache.add(forPlayer(e.player))
			}

			@EventHandler(priority = MONITOR)
			fun onPlayerQuit(e: PlayerQuitEvent) {
				val d = forPlayer(e.player)
				save(d)
				strongCache.remove(d)
			}
		}, plugin)

		// Save one weakly cached & dirty player per tick
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			saveNWeakDirty(1)
		}, 0, 1)
	}

	override fun saveAllDirty() {
		dirty.forEach { saveRaw(it) }
		dirty.clear()
	}

	private fun saveNWeakDirty(n: Int) {
		val entries = dirty.asSequence().filter { !strongCache.contains(it) }.take(n).toList()
		entries.forEach { save(it) }
	}

	override fun markDirty(v: DatabasePlayerData) {
		dirty.add(v)
	}

	override fun save(v: DatabasePlayerData) {
		saveRaw(v)
		dirty.remove(v)
	}

	private fun saveRaw(v: DatabasePlayerData) {
		val stmnt = fablesDatabase.prepareStatement("UPDATE fables_players SET " +
				"current_character = ?, " +
				"chat_channel = ? " +
				"WHERE id = ?")
		val currentCharacterId = v.currentCharacterId
		if (currentCharacterId != null) {
			stmnt.setLong(1, currentCharacterId.toLong())
		} else {
			stmnt.setNull(1, Types.BIGINT)
		}
		stmnt.setString(2, v.chatChannel)
		stmnt.setObject(3, v.offlinePlayer.uniqueId)
		stmnt.executeUpdate()
	}

	override fun destroy(v: DatabasePlayerData) {
		cache.remove(v.offlinePlayer.uniqueId)
		strongCache.remove(v)
		val stmnt = fablesDatabase.prepareStatement("DELETE FROM fables_players WHERE id = ?")
		stmnt.setObject(1, v.offlinePlayer.uniqueId)
		stmnt.executeUpdate()
	}

	fun forPlayer(p: OfflinePlayer): DatabasePlayerData {
		val id = p.uniqueId
		return fromCache(id) ?: run {
			val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_players WHERE id = ?")
			stmnt.setObject(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) throw Exception("Player not found in database")
			val playerData = fromRow(result)
			cache[id] = WeakReference(playerData)
			playerData
		}
	}

	private fun fromCache(id: UUID): DatabasePlayerData? {
		val maybe = cache[id]?.get()
		if (maybe == null) cache.remove(id)
		return maybe
	}

	private fun fromRowOrCache(result: ResultSet): DatabasePlayerData {
		val id = result.getObject("id") as UUID
		val maybe = fromCache(id)

		return if (maybe != null) {
			maybe
		} else {
			val surely = fromRow(result)
			cache[id] = WeakReference(surely)
			surely
		}
	}

	private fun fromRow(result: ResultSet): DatabasePlayerData {
		val id = result.getObject("id") as UUID
		var currentCharacterId: ULong? = result.getLong("current_character").toULong()
		if (result.wasNull()) currentCharacterId = null
		val chatChannel = result.getString("chat_channel")

		return DatabasePlayerData(this, server.getOfflinePlayer(id), currentCharacterId, chatChannel)
	}
}

fun DatabasePlayerData.Companion.forOfflinePlayer(p: OfflinePlayer) = databasePlayerRepository.forPlayer(p)
fun DatabasePlayerData.save() = databasePlayerRepository.save(this)
fun DatabasePlayerData.destroy() = databasePlayerRepository.destroy(this)
