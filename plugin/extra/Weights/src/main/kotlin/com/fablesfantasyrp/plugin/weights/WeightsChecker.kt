package com.fablesfantasyrp.plugin.weights

import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.every
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.Job
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration
import kotlin.time.toKotlinDuration

class WeightsChecker(private val plugin: Plugin,
					 private val config: WeightsConfig,
					 private val profileManager: ProfileManager,
					 private val characters: CharacterRepository) {
	private val server = plugin.server
	private lateinit var job: Job

	fun start() {
		job = every(plugin, Duration.ofMillis(50).toKotlinDuration()) {
			for (player in server.onlinePlayers) {

				val items = player.inventory.contents.filterNotNull()
				val weight = calculateWeight(items, config)

				flaunch { applyWeight(player, weight, calculateCap(player)) }
			}
		}
	}

	fun stop() {
		job.cancel()
	}

	private suspend fun calculateCap(player: Player): Int {
		val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) }
		return config.cap + if (character != null && character.traits.contains(CharacterTrait.PACKMULE)) 20 else 0
	}
}
