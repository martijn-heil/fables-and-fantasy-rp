package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.TransactionStep
import com.fablesfantasyrp.plugin.utils.every
import kotlinx.coroutines.flow.onEach
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration
import java.time.Instant
import kotlin.time.toKotlinDuration

class NaturallyStealthy(plugin: Plugin,
						characters: CharacterRepository,
						profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.NATURALLY_STEALTHY, plugin, characters, profileManager) {
	private val effect = PotionEffect(PotionEffectType.INVISIBILITY, 600, 1, false, true, false)
	private val lastMoved = HashMap<Player, Instant>()
	private val invisible = HashSet<Player>()

	override fun init() {
		super.init()

		server.pluginManager.registerEvents(NaturallyStealthyListener(), plugin)

		every(plugin, Duration.ofMillis(50).toKotlinDuration()) {
			getPlayersWithTrait()
				.onEach {
					val lastMoved = lastMoved[it.player]
					if (lastMoved != null && Duration.between(lastMoved, Instant.now()).seconds > 3) {
						if (it.player.isSneaking) setInvisible(it.player, true)
					} else {
						setInvisible(it.player, false)
					}
				}

				invisible.forEach { it.addPotionEffect(effect) }
		}
	}

	fun isInvisible(player: Player) = invisible.contains(player)

	private fun setInvisible(player: Player, value: Boolean) {
		if (value) {
			invisible.add(player)
			player.addPotionEffect(effect)
		} else {
			invisible.remove(player)
			player.removePotionEffect(PotionEffectType.INVISIBILITY)
		}
	}

	inner class NaturallyStealthyListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerProfileChange(e: PlayerSwitchProfileEvent) {
			val oldValue = isInvisible(e.player)

			e.transaction.steps.add(TransactionStep(
				{ setInvisible(e.player, false) },
				{ setInvisible(e.player, oldValue) }
			))
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerMove(e: PlayerMoveEvent) {
			if (e.hasExplicitlyChangedPosition()) {
				lastMoved[e.player] = Instant.now()
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			lastMoved.remove(e.player)
			setInvisible(e.player, false)
		}
	}
}
