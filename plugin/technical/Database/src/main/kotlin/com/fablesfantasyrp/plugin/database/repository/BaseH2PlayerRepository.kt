package com.fablesfantasyrp.plugin.database.repository

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.PlayerRepository
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import java.util.*
import javax.sql.DataSource

abstract class BaseH2PlayerRepository<T: Identifiable<UUID>>(private val dataSource: DataSource, private val plugin: Plugin)
	: BaseH2KeyedRepository<UUID, T>(UUID::class.java, plugin, dataSource), PlayerRepository<T> {
	private val server = plugin.server

	override fun forPlayer(player: OfflinePlayer): T = this.forId(player.uniqueId)!!

	override fun forId(id: UUID): T? {
		return this.forIdMaybe(id) ?: createSafe(id)
	}

	fun forIdMaybe(id: UUID): T? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) { return@use null }
			fromRow(result)
		}
	}

	/**
	 * Only create if the uuid is a valid player that has played before.
	 */
	private fun createSafe(id: UUID): T? {
		val offlinePlayer = server.getOfflinePlayer(id)
		return if (offlinePlayer.isOnline || offlinePlayer.hasPlayedBefore()) {
			this.create(id)
		} else null
	}

	protected abstract fun create(id: UUID): T
}
