package com.fablesfantasyrp.plugin.utils.extensions.bukkit

import net.minecraft.network.protocol.game.ClientboundGameEventPacket
import net.minecraft.world.entity.item.ItemEntity
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
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
	val entityItem = ItemEntity(p.level, p.x, d0, p.z, (itemStack as CraftItemStack).handle)

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
