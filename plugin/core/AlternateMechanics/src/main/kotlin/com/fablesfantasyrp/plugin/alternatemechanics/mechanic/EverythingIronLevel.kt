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
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.*

class EverythingIronLevel(private val plugin: Plugin): Mechanic {
	private val server = plugin.server

	// These UUID's are just randomly generated but will help us in the future
	// if we ever need to do a migration on these attributes.
	private val UUID_GENERIC_ATTACK_DAMAGE = UUID.fromString("fb706102-9a2b-4305-962d-268ef0ffe2d9")
	private val UUID_GENERIC_ATTACK_SPEED = UUID.fromString("6cb7bcbe-2ac2-4f66-ba92-f1dabc3d59d8")
	private val UUID_GENERIC_ARMOR = UUID.fromString("7ba3e90e-f199-4b57-98bf-a09ef05d499e")
	private val UUID_GENERIC_ARMOR_TOUGHNESS = UUID.fromString("34b6fe8c-89e3-417d-a062-e3955ea1e07a")
	private val UUID_GENERIC_KNOCKBACK_RESISTANCE = UUID.fromString("aebdf880-812b-4366-941e-71655c43029e")

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
					AttributeModifier(
						UUID_GENERIC_ATTACK_DAMAGE,
						"generic.attack_damage",
						-5.0,
						AttributeModifier.Operation.ADD_NUMBER,
						EquipmentSlot.HAND
					)
				)

				meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
					AttributeModifier(
						UUID_GENERIC_ATTACK_SPEED,
						"generic.attack_speed",
						-2.5,
						AttributeModifier.Operation.ADD_NUMBER,
						EquipmentSlot.HAND
					)
				)
			}

			Material.DIAMOND_AXE, Material.NETHERITE_AXE-> {
				meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
					AttributeModifier(
						UUID_GENERIC_ATTACK_DAMAGE,
						"generic.attack_damage",
						8.0,
						AttributeModifier.Operation.ADD_NUMBER,
						EquipmentSlot.HAND
					)
				)

				meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
					AttributeModifier(
						UUID_GENERIC_ATTACK_SPEED,
						"generic.attack_speed",
						-3.0,
						AttributeModifier.Operation.ADD_NUMBER,
						EquipmentSlot.HAND
					)
				)
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

				val slot = itemStack.type.equipmentSlot

				meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
					AttributeModifier(
						UUID_GENERIC_ARMOR,
						"generic.armor",
						genericArmorAdd,
						AttributeModifier.Operation.ADD_NUMBER,
						slot
					)
				)

				meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
					AttributeModifier(
						UUID_GENERIC_ARMOR_TOUGHNESS,
						"generic.armor_toughness",
						0.00,
						AttributeModifier.Operation.ADD_NUMBER,
						slot
					)
				)

				meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
					AttributeModifier(
						UUID_GENERIC_KNOCKBACK_RESISTANCE,
						"generic.knockback_resistance",
						0.00,
						AttributeModifier.Operation.ADD_NUMBER,
						slot
					)
				)
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
