package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin

class FarmReplant(private val plugin: Plugin,
				  private val worldGuard: WorldGuardPlugin,
				  private val regionContainer: RegionContainer) : Mechanic {
	private val server = plugin.server
	private val crops = hashSetOf(Material.WHEAT, Material.CARROTS, Material.BEETROOTS, Material.POTATOES)

	override fun init() {
		server.pluginManager.registerEvents(FarmReplantListener(), plugin)
	}

	inner class FarmReplantListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerFarm(e: PlayerInteractEvent) {
			if (e.hand != EquipmentSlot.HAND) return
			if (e.player.inventory.itemInMainHand.type != Material.WOODEN_HOE) return
			val block = e.clickedBlock ?: return
			if (!crops.contains(block.type)) return

			val canBuild = regionContainer.createQuery().testBuild(BukkitAdapter.adapt(block.location), worldGuard.wrapPlayer(e.player))
			if (!canBuild) return

			val blockData = (block.blockData as Ageable)
			if (blockData.age == 0) return

			val location = block.location
			val tool = e.player.inventory.itemInMainHand
			val drops = block.getDrops(tool, e.player)

			blockData.age = 0
			block.blockData = blockData

			location.world.playSound(location, Sound.BLOCK_CROP_BREAK, 1.0f, 1.0f)
			drops.forEach { location.world.dropItem(location, it) }
		}
	}
}
