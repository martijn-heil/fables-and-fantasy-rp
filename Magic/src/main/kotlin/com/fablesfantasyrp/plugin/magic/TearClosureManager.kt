package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.magic.domain.entity.Tear
import com.fablesfantasyrp.plugin.magic.domain.repository.EntityTearRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.distanceSafe
import org.bukkit.plugin.Plugin

class TearClosureManager(private val plugin: Plugin,
						 private val tearRepository: EntityTearRepository<*>,
						 private val profileManager: ProfileManager) {
	private val server = plugin.server
	private val tearsScheduledForRemoval = HashMap<Tear, Long>()

	init {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			tearsScheduledForRemoval
					.filter { System.currentTimeMillis() - it.value >= 60 * 10 * 1000 }
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
		val owner = tear.owner
		val player = profileManager.getCurrentForProfile(owner.profile)
		return player == null || owner.profile.location.distanceSafe(tear.location) > 15.0
	}
}
