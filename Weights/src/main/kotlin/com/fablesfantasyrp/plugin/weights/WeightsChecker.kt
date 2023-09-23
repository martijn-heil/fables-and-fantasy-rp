package com.fablesfantasyrp.plugin.weights

import com.fablesfantasyrp.plugin.characters.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class WeightsChecker(private val plugin: Plugin,
					 private val config: WeightsConfig,
					 private val profileManager: ProfileManager,
					 private val characters: CharacterRepository,
					 private val characterTraits: CharacterTraitRepository) {
	private val server = plugin.server
	private var taskId: Int = -1

	fun start() {
		taskId = server.scheduler.scheduleSyncRepeatingTask(plugin, {
			for (player in server.onlinePlayers) {

				val items = player.inventory.contents.filterNotNull()
				val weight = calculateWeight(items, config)

				applyWeight(player, weight, calculateCap(player))
			}
		}, 0, 1)
		check(taskId != -1)
	}

	fun stop() {
		server.scheduler.cancelTask(taskId)
	}

	private fun calculateCap(player: Player): Int {
		val packMule = characterTraits.forId(KnownCharacterTraits.PACK_MULE)!!
		val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) }
		return config.cap + if (character != null && characterTraits.hasTrait(character, packMule)) 20 else 0
	}
}
