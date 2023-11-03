package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class EverythingIronLevel(private val plugin: Plugin): Mechanic {
	private val server = plugin.server

	private val materials = hashSetOf(
		Material.DIAMOND_SWORD,
		Material.NETHERITE_SWORD,
		Material.DIAMOND_AXE,
		Material.NETHERITE_AXE,
		Material.DIAMOND_HELMET,
		Material.NETHERITE_HELMET,
		Material.DIAMOND_CHESTPLATE,
		Material.NETHERITE_CHESTPLATE,
		Material.DIAMOND_LEGGINGS,
		Material.NETHERITE_LEGGINGS,
		Material.DIAMOND_BOOTS,
		Material.NETHERITE_BOOTS)

	override fun init() {
		server.pluginManager.registerEvents(EverythingIronLevelListener(), plugin)
	}

	private fun apply(itemStack: ItemStack) {
		if (!materials.contains(itemStack.type)) return
		val meta = itemStack.itemMeta

		if (meta.hasAttributeModifiers()) return

		when (itemStack.type) {
			Material.DIAMOND_SWORD, Material.NETHERITE_SWORD -> {
				meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
					AttributeModifier("generic.attack_damage", -5.0, AttributeModifier.Operation.ADD_NUMBER))

				meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
					AttributeModifier("generic.attack_speed", -2.5, AttributeModifier.Operation.ADD_NUMBER))
			}

			Material.DIAMOND_AXE, Material.NETHERITE_AXE-> {
				meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
					AttributeModifier("generic.attack_damage", 8.0, AttributeModifier.Operation.ADD_NUMBER))

				meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
					AttributeModifier("generic.attack_speed", -3.0, AttributeModifier.Operation.ADD_NUMBER))
			}

			Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
			Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS -> {
				val genericArmorAdd = when (itemStack.type) {
					Material.DIAMOND_HELMET, Material.NETHERITE_HELMET -> 2.0
					Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE -> 6.0
					Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS -> 5.0
					Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS -> 2.0
					else -> 0.0
				}

				meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
					AttributeModifier("generic.armor", genericArmorAdd, AttributeModifier.Operation.ADD_NUMBER))

				meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
					AttributeModifier("generic.armor_toughness", 0.00, AttributeModifier.Operation.ADD_NUMBER))

				meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
					AttributeModifier("generic.knockback_resistance", 0.00, AttributeModifier.Operation.ADD_NUMBER))
			}
			else -> {}
		}

		itemStack.itemMeta = meta
	}

	inner class EverythingIronLevelListener : Listener {
		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onInventoryClick(e: InventoryClickEvent) {
			e.currentItem?.let { apply(it) }
			e.cursor?.let { apply(it) }
		}

		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onPlayerDropItem(e: PlayerDropItemEvent) {
			if (e.player.gameMode == GameMode.CREATIVE) {
				apply(e.itemDrop.itemStack)
			}
		}
	}
}
