package com.fablesfantasyrp.plugin.targeting.data

import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository
import com.fablesfantasyrp.plugin.targeting.GlowingManager
import com.fablesfantasyrp.plugin.targeting.Permission
import org.bukkit.OfflinePlayer

class MemorySimpleTargetingPlayerDataRepository(private val glowingManager: GlowingManager) :
		SimpleMapRepository<OfflinePlayer, SimpleTargetingPlayerData>(), SimpleTargetingPlayerDataRepository {
	override fun update(v: SimpleTargetingPlayerData) {
		val current = this.forId(v.id)!!
		super.update(v)

		val offlinePlayer = v.id
		val player = offlinePlayer.player
		if (offlinePlayer.isOnline && player != null) {
			val removed = current.targets.minus(v.targets.toSet())
			val added = v.targets.minus(current.targets.toSet())

			if (player.hasPermission(Permission.Glowingvisuals)) {
				removed.forEach { glowingManager.unglowFor(it, player) }
				added.forEach { glowingManager.glowFor(it, player) }
			}
		}
	}

	override fun forId(id: OfflinePlayer): SimpleTargetingPlayerData? {
		return super.forId(id) ?: SimpleTargetingPlayerData(id, emptySet(), false)
	}
}
