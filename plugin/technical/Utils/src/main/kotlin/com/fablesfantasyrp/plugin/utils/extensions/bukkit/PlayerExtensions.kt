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
package com.fablesfantasyrp.plugin.utils.extensions.bukkit

import net.minecraft.network.protocol.game.ClientboundGameEventPacket
import net.minecraft.world.entity.item.ItemEntity
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack

fun Player.showEndCredits() {
	if (this !is CraftPlayer) return
	this.handle.wonGame = true
	this.handle.connection.send(ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 1f))
}

fun Player.tryFakeDrop(itemStack: ItemStack): Boolean {
	val p = (this as CraftPlayer).handle
	val d0: Double = p.eyeY - 0.30000001192092896
	val entityItem = ItemEntity(p.level(), p.x, d0, p.z, (itemStack as CraftItemStack).handle)

	val drop = entityItem.bukkitEntity as Item
	val event = PlayerDropItemEvent(this, drop)

	Bukkit.getPluginManager().callEvent(event)

	return !event.isCancelled
}

// Citizens NPC's are instances of Player too, but obviously, not real players
// Citizens NPC's don't show up in the server.onlinePlayers list
val Player.isRealPlayer: Boolean get() = server.onlinePlayers.contains(player)

// This code is supported by SuperVanish, PremiumVanish, VanishNoPacket and a few more vanish plugins.
val Player.isVanished: Boolean
	get() = getMetadata("vanished").find { it.asBoolean() } != null
