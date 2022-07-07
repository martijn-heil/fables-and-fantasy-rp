package com.fablesfantasyrp.plugin.playerdata.database

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.repository.CachingRepository
import com.fablesfantasyrp.plugin.playerdata.data.PlayerData
import com.fablesfantasyrp.plugin.playerdata.data.PlayerDataRepository
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.LOWEST
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.lang.ref.WeakReference
import java.sql.ResultSet
import java.sql.Types
import java.util.*


class DatabasePlayerRepository internal constructor(private val plugin: Plugin)
	: PlayerDataRepository, CachingRepository<PlayerData> {
	private val cache = HashMap<UUID, WeakReference<PlayerData>>()
	private val strongCache = HashSet<PlayerData>()
	private val dirty = LinkedHashSet<PlayerData>()
	private val server = plugin.server

	init {
		server.pluginManager.registerEvents(object : Listener {
			@EventHandler(priority = LOWEST)
			fun onPlayerJoin(e: PlayerJoinEvent) {
				strongCache.add(forOfflinePlayer(e.player))
			}

			@EventHandler(priority = MONITOR)
			fun onPlayerQuit(e: PlayerQuitEvent) {
				val d = forOfflinePlayer(e.player)
				save(d)
				strongCache.remove(d)
			}
		}, plugin)

		// Save one weakly cached & dirty player per tick
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			saveNWeakDirty(1)
		}, 0, 1)

		server.onlinePlayers.forEach { strongCache.add(forOfflinePlayer(it)) }
	}

	override fun saveAllDirty() {
		synchronized(this) {
			dirty.forEach { saveRaw(it) }
			dirty.clear()
		}
	}

	private fun saveNWeakDirty(n: Int) {
		synchronized(this) {
			val entries = dirty.asSequence().filter { !strongCache.contains(it) }.take(n).toList()
			entries.forEach { save(it) }
		}
	}

	override fun markDirty(v: PlayerData) {
		synchronized(this) {
			dirty.add(v)
		}
	}

	override fun save(v: PlayerData) {
		synchronized(this) {
			saveRaw(v)
			dirty.remove(v)
		}
	}

	private fun saveRaw(v: PlayerData) {
		synchronized(this) {
			plugin.logger.info("Saving player '${v.offlinePlayer.name}' (${v.offlinePlayer.uniqueId}) to database..")
			val stmnt = fablesDatabase.prepareStatement("UPDATE fables_players SET " +
					"current_character = ?, " +
					"chat_channel = ?, " +
					"chat_style = ?, " +
					"chat_disabled_channels = ? " +
					"WHERE id = ?")
			val currentCharacterId = v.currentCharacterId
			if (currentCharacterId != null) {
				stmnt.setLong(1, currentCharacterId.toLong())
			} else {
				stmnt.setNull(1, Types.BIGINT)
			}
			stmnt.setString(2, v.chatChannel)
			stmnt.setString(3, v.chatStyle?.let { GsonComponentSerializer.gson().serialize(Component.text().style(it).build()) } )
			stmnt.setArray(4, fablesDatabase.createArrayOf("VARCHAR(32)", v.chatDisabledChannels.toTypedArray()))
			stmnt.setObject(5, v.offlinePlayer.uniqueId)
			stmnt.executeUpdate()
		}
	}

	override fun destroy(v: PlayerData) {
		synchronized(this) {
			cache.remove(v.offlinePlayer.uniqueId)
			strongCache.remove(v)
			val stmnt = fablesDatabase.prepareStatement("DELETE FROM fables_players WHERE id = ?")
			stmnt.setObject(1, v.offlinePlayer.uniqueId)
			stmnt.executeUpdate()
		}
	}

	override fun forOfflinePlayer(p: OfflinePlayer): PlayerData {
		synchronized(this) {
			val id = p.uniqueId
			return fromCache(id) ?: run {
				while(true) {
					val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_players WHERE id = ?")
					stmnt.setObject(1, id)
					val result = stmnt.executeQuery()
					if (!result.next()) {
						plugin.logger.info("Player '${p.name}' (${p.uniqueId}) did not yet exist in database, creating..")
						this.create(p)
						continue // Try again
					}
					plugin.logger.info("Loading player '${p.name}' (${p.uniqueId}) from database..")
					val playerData = fromRow(result)
					cache[id] = WeakReference(playerData)
					return playerData
				}
				throw IllegalStateException()
			}
		}
	}

	override fun allOnline(): Collection<PlayerData> = strongCache

	override fun all(): Collection<PlayerData> {
		val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_players")
		val result = stmnt.executeQuery()
		val all = ArrayList<PlayerData>()
		while (result.next()) all.add(fromRowOrCache(result))
		return all
	}

	private fun fromCache(id: UUID): PlayerData? {
		synchronized(this) {
			val maybe = cache[id]?.get()
			if (maybe == null) cache.remove(id)
			return maybe
		}
	}

	private fun fromRowOrCache(result: ResultSet): PlayerData {
		synchronized(this) {
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
	}

	private fun fromRow(result: ResultSet): PlayerData {
		synchronized(this) {
			val id = result.getObject("id") as UUID
			var currentCharacterId: ULong? = result.getLong("current_character").toULong()
			if (result.wasNull()) currentCharacterId = null
			val chatChannel = result.getString("chat_channel")
			val chatStyle = result.getString("chat_style")?.let { GsonComponentSerializer.gson().deserialize(it).style() }
			val chatDisabledChannels = result.getArray("chat_disabled_channels")?.array
					?.let { it as Array<Any> }
					?.let { it.map { it as String } }?.toSet() ?: emptySet()

			return DatabasePlayerData(this, server.getOfflinePlayer(id), currentCharacterId,
					chatChannel, chatStyle, chatDisabledChannels)
		}
	}

	private fun create(p: OfflinePlayer) {
		synchronized(this) {
			val stmnt2 = fablesDatabase.prepareStatement("INSERT INTO fables_players (id) VALUES(?)")
			stmnt2.setObject(1, p.uniqueId)
			stmnt2.executeUpdate()
			stmnt2.close()
		}
	}
}
