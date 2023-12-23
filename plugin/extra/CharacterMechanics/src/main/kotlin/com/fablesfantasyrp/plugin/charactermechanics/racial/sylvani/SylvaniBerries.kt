package com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani

import com.fablesfantasyrp.plugin.charactermechanics.SYSPREFIX
import com.fablesfantasyrp.plugin.charactermechanics.flaunch
import com.fablesfantasyrp.plugin.charactermechanics.racial.base.BaseRaceBehavior
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isRealPlayer
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.ocpsoft.prettytime.PrettyTime
import java.time.Duration
import java.time.Instant
import kotlin.random.Random

// Players with this trait get two extra points in the strength stat.
class SylvaniBerries(plugin: Plugin,
					 characters: CharacterRepository,
					 profileManager: ProfileManager)
	: BaseRaceBehavior(Race.SYLVANI, plugin, characters, profileManager) {
	private val harvested = HashMap<Int, Instant>()

	override fun init() {
		super.init()
		server.pluginManager.registerEvents(SylvaniBerriesListener(), plugin)
	}

	inner class SylvaniBerriesListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerRightClickSylvani(e: PlayerInteractAtEntityEvent) {
			flaunch {
				if (e.hand != EquipmentSlot.HAND || e.player.isSneaking) return@flaunch
				val harvester = e.player
				val player = e.rightClicked as? Player ?: return@flaunch
				if (!player.isRealPlayer) return@flaunch
				val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return@flaunch
				if (character.race != Race.SYLVANI) return@flaunch
				val nextHarvestTime = harvested[character.id]?.plus(Duration.ofHours(1)) ?: Instant.now()

				if (nextHarvestTime > Instant.now()) {
					harvester.sendMessage("$SYSPREFIX You can harvest ${character.name}'s berries again " +
						PrettyTime().format(nextHarvestTime))
					return@flaunch
				}

				val location = player.location
				val world = location.world
				val isEpicDrop = Random.nextInt(1, 101) > 98

				if (isEpicDrop) {
					val leftOver = harvester.inventory.addItem(ItemStack(Material.GLOW_BERRIES, 8))
					leftOver.values.forEach { world.dropItem(location, it) }
					world.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
				} else {
					val leftOver = harvester.inventory.addItem(ItemStack(Material.SWEET_BERRIES, 8))
					leftOver.values.forEach { world.dropItem(location, it) }
					world.playSound(location, Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 1.0f, 1.0f)
				}

				harvested[character.id] = Instant.now()
			}
		}
	}
}
