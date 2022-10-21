package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.magic.data.entity.EntityTearRepository
import com.fablesfantasyrp.plugin.magic.data.entity.Tear
import org.bukkit.plugin.Plugin

class TearClosureManager(private val plugin: Plugin, private val tearRepository: EntityTearRepository<*>) {
	private val server = plugin.server
	private val tearsScheduledForRemoval = HashMap<Tear, Long>()

	init {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			tearsScheduledForRemoval
					.filter { System.currentTimeMillis() - it.value >= 60 * 1 * 1000 }
					.forEach { tearsScheduledForRemoval.remove(it.key); tearRepository.destroy(it.key) }

			val all = tearRepository.all()

			for (tear in all) {
				if (shouldTearClose(tear)) {
					tearsScheduledForRemoval.putIfAbsent(tear, System.currentTimeMillis())
				} else {
					tearsScheduledForRemoval.remove(tear)
				}
			}
		}, 0, 20)
	}

	private fun shouldTearClose(tear: Tear): Boolean {
		val offlinePlayer = tear.owner.playerCharacter.player
		val player = offlinePlayer.player ?: return false
		return (!player.isOnline ||
				player.currentPlayerCharacter != tear.owner.playerCharacter ||
				player.location.world != tear.location.world ||
				player.location.distance(tear.location) > 15.0)
	}
}
