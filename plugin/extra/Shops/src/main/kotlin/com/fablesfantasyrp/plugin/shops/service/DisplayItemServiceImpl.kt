package com.fablesfantasyrp.plugin.shops.service

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.data.type.Slab
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemDespawnEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.floor

class DisplayItemServiceImpl(private val plugin: Plugin) : DisplayItemService {
	private val server = plugin.server

	private val hovering = HashMap<BlockIdentifier, Item>()
	private val itemIds = HashSet<UUID>()
	private val key = NamespacedKey.fromString("fablesfantasyrp:shops_display_item")!!

	override fun spawnDisplayItem(location: BlockIdentifier, item: ItemStack) {
		val hoverItem = item.asOne()
		val itemMeta = hoverItem.itemMeta ?: server.itemFactory.getItemMeta(hoverItem.type)
		if (itemMeta == null) {
			plugin.logger.info("itemMeta is null: type: ${hoverItem.type}")
		}
		val persistentDataContainer = itemMeta.persistentDataContainer
		persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
		hoverItem.itemMeta = itemMeta

		val world = server.getWorld(location.world)!!
		val block = world.getBlockAt(location.x, location.y, location.z)
		val hoverHeight = getHoverHeight(block)
		val hoverLocation = location.toLocation().toCenterLocation().apply { y = floor(y) + hoverHeight.toDouble() }

		val spawnedItem = world.dropItem(hoverLocation, hoverItem)
		spawnedItem.pickupDelay = Int.MAX_VALUE
		spawnedItem.velocity = Vector(0, 0, 0)
		spawnedItem.setGravity(false)
		spawnedItem.isInvulnerable = true
		hovering[location] = spawnedItem
		itemIds.add(spawnedItem.uniqueId)
	}

	override fun isDisplayItem(item: Item) = itemIds.contains(item.uniqueId)

	private fun isStrayDisplayItem(item: Item)
		= !itemIds.contains(item.uniqueId) && item.itemStack.itemMeta.persistentDataContainer.has(key)

	private fun isProtectedItem(item: Item)
		= isDisplayItem(item) || item.itemStack.itemMeta.persistentDataContainer.has(key)

	fun start() {
		server.pluginManager.registerEvents(DisplayItemListener(), plugin)
	}

	fun stop() {
		hovering.values.forEach { it.remove() }
		hovering.clear()
		itemIds.clear()
	}

	private fun getHoverHeight(block: Block): Float {
		val blockData = block.blockData
		return if (blockData is Slab) {
			if (blockData.type == Slab.Type.TOP) {
				1.1f
			} else {
				0.6f
			}
		} else {
			1.1f
		}
	}

	inner class DisplayItemListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL)
		fun onItemDespawn(e: ItemDespawnEvent) {
			if (isDisplayItem(e.entity)) e.isCancelled = true
		}

		@EventHandler(priority = EventPriority.NORMAL)
		fun onHopperPickup(e: InventoryPickupItemEvent) {
			if (isProtectedItem(e.item)) e.isCancelled = true
		}

		@EventHandler(priority = EventPriority.NORMAL)
		fun onPlayerFish(e: PlayerFishEvent) {
			if (e.caught is Item && isProtectedItem(e.caught as Item)) e.isCancelled = true
		}
	}
}
