/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.magic.domain.entity.Tear
import com.fablesfantasyrp.plugin.magic.domain.repository.TearRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import org.bukkit.plugin.Plugin

class TearClosureManager(private val plugin: Plugin,
						 private val tearRepository: TearRepository,
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
