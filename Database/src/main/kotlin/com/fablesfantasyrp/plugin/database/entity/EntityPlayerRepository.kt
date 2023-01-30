package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.OnlinePlayerCacheMarker
import com.fablesfantasyrp.plugin.database.repository.*
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import java.util.*

open class EntityPlayerRepository<C, T: Identifiable<UUID>>(child: C, private val plugin: Plugin)
	: SimpleEntityRepository<UUID, T, C>(child), PlayerRepository<T>
		where C: KeyedRepository<UUID, T>,
			  C: MutableRepository<T>,
			  C: HasDirtyMarker<T> {

	override fun forPlayer(player: OfflinePlayer) = this.forId(player.uniqueId)!!

	override fun init() {
		super.init()
		OnlinePlayerCacheMarker(plugin, this) { p -> forId(p.uniqueId)!! }.start()
	}
}
