package com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani

import com.fablesfantasyrp.plugin.charactermechanics.racial.base.BaseRaceBehavior
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.inventory.data.entity.FablesInventoryRepository
import com.fablesfantasyrp.plugin.location.data.entity.ProfileLocationRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.TransactionStep
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.customModel
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.itemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.meta
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.Plugin

// Players with this trait get two extra points in the strength stat.
class SylvaniAntlers(plugin: Plugin,
					 characters: CharacterRepository,
					 profileManager: ProfileManager,
					 private val profileLocations: ProfileLocationRepository,
					 private val profileInventories: FablesInventoryRepository)
	: BaseRaceBehavior(Race.SYLVANI, plugin, characters, profileManager) {
	private val HELMET_SLOT = 39
	private val ANTLERS_ITEM = itemStack(Material.INK_SAC) {
		meta {
			displayName(Component.text("Sylvani Antlers").decoration(TextDecoration.ITALIC, false))
			customModel = 1
		}
	}

	override fun init() {
		super.init()
		server.pluginManager.registerEvents(SylvaniAntlersListener(), plugin)
	}

	inner class SylvaniAntlersListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerProfileChange(e: PlayerSwitchProfileEvent) {
			val newProfile = e.new
			val newCharacter = newProfile?.let { characters.forProfile(it) } ?: return
			if (newCharacter.race != Race.SYLVANI) return

			val location = profileLocations.forOwner(newProfile).location
			val inventory = profileInventories.forOwner(newProfile).inventory

			e.transaction.steps.add(TransactionStep(
				{
					val oldHelmet = inventory.helmet
					if (oldHelmet != null && oldHelmet != ANTLERS_ITEM) {
						location.world.dropItem(location, oldHelmet)
						inventory.helmet = null
					}

					inventory.helmet = ANTLERS_ITEM
				}, {}
			))
		}

		// Don't allow the antlers to be taken off
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onInventoryClickEvent(e: InventoryClickEvent) {
			val clickedInventory = e.clickedInventory ?: return
			if (clickedInventory.type != InventoryType.PLAYER) return
			if (e.slot != HELMET_SLOT && e.currentItem != ANTLERS_ITEM) return
			val player = e.whoClicked as? Player ?: return
			val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return
			if (character.race != Race.SYLVANI) return

			e.isCancelled = true
		}
	}
}
