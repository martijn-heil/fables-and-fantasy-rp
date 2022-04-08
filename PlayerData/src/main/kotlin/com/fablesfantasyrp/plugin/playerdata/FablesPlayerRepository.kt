package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.characters.playerCharacterRepository
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.repository.CachingRepository
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.lang.ref.WeakReference
import java.sql.ResultSet
import java.util.*


class FablesPlayerRepository internal constructor(private val plugin: Plugin) : CachingRepository<FablesPlayer> {
	private val cache = HashMap<UUID, WeakReference<FablesPlayer>>()
	private val strongCache = HashSet<FablesPlayer>()
	private val dirty = LinkedHashSet<FablesPlayer>()

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

	override fun markDirty(v: FablesPlayer) {
		dirty.add(v)
	}

	override fun save(v: FablesPlayer) {
		saveRaw(v)
		dirty.remove(v)
	}

	private fun saveRaw(v: FablesPlayer) {
		val stmnt = fablesDatabase.prepareStatement("UPDATE fables_players SET " +
				"current_character = ?, " +
				"chat_channel = ? " +
				"WHERE id = ?")
		stmnt.setLong(1, v.currentCharacter.id.toLong())
		stmnt.setString(2, v.chatChannel)
		stmnt.setObject(3, v.player.uniqueId)
		stmnt.executeUpdate()
	}

	override fun destroy(v: FablesPlayer) {
		cache.remove(v.player.uniqueId)
		strongCache.remove(v)
		val stmnt = fablesDatabase.prepareStatement("DELETE FROM fables_players WHERE id = ?")
		stmnt.setObject(1, v.player.uniqueId)
		stmnt.executeUpdate()
	}

	fun forPlayer(p: OfflinePlayer): FablesPlayer {
		val id = p.uniqueId
		return fromCache(id) ?: run {
			val stmnt = fablesDatabase.prepareStatement("SELECT * FROM fables_characters WHERE id = ?")
			stmnt.setObject(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) throw Exception("Character not found in database")
			val char = fromRow(result)
			cache[id] = WeakReference(char)
			char
		}
	}

	private fun fromCache(id: UUID): FablesPlayer? {
		val maybe = cache[id]?.get()
		if (maybe == null) cache.remove(id)
		return maybe
	}

	private fun fromRowOrCache(result: ResultSet): FablesPlayer {
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

	private fun fromRow(result: ResultSet): FablesPlayer {
		val id = result.getObject("id") as UUID
		val currentCharacterId = result.getLong("current_character").toULong()
		val chatChannel = result.getString("chat_channel")

		val char = playerCharacterRepository.forId(currentCharacterId)

		return FablesPlayer(this, server.getOfflinePlayer(id), char, chatChannel)
	}
}
