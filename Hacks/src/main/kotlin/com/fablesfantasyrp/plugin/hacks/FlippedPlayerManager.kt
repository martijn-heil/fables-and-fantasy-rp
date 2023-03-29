package com.fablesfantasyrp.plugin.hacks

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerShowEntityEvent
import org.bukkit.plugin.Plugin

class FlippedPlayerManager(private val plugin: Plugin) : Listener {
	private val server = plugin.server
	private val flippedPlayers = HashSet<Player>()
	private val reloadingPlayers = HashSet<Player>()

	private class FlippedPlayer(val player: Player, val profile: GameProfile, val flippedProfile: GameProfile)

	fun start() {
		server.pluginManager.registerEvents(this, plugin)
	}

	fun stop() {
		flippedPlayers.forEach { setFlippedAppearanceFor(it, false, server.onlinePlayers.filter { it2 -> it.uniqueId != it2.uniqueId }) }
		flippedPlayers.clear()
	}

	fun isFlipped(player: Player) = flippedPlayers.contains(player)

	fun setFlipped(player: Player, flipped: Boolean) {
		if (flipped == isFlipped(player)) return
		setFlippedAppearanceFor(player, flipped, server.onlinePlayers.filter { player.uniqueId != it.uniqueId })
		if (flipped) {
			flippedPlayers.add(player)
		} else {
			flippedPlayers.remove(player)
		}
	}

	private fun setFlippedAppearanceFor(player: Player, flipped: Boolean, observers: Collection<Player>) {
		check(player is CraftPlayer)
		plugin.logger.info("setFlippedFor(${player.name}, $flipped)")
		if (flipped) {
			val realName = player.name
			val realProfile = player.profile

			player.handle.gameProfile = makeFlippedProfile(realProfile)
			player.setDisplayName(realName)
			player.setPlayerListName(realName)
			observers.forEach { reloadPlayer(player, it) }
			player.handle.gameProfile = realProfile
		} else {
			observers.forEach { reloadPlayer(player, it) }
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onLeave(e: PlayerQuitEvent) {
		flippedPlayers.remove(e.player)
		reloadingPlayers.remove(e.player)
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onJoin(e: PlayerJoinEvent) {
		val observer = e.player
		check(observer is CraftPlayer)
		server.scheduler.scheduleSyncDelayedTask(plugin) {
			flippedPlayers
					.filter { observer.canSee(it) }
					.forEach { setFlippedAppearanceFor(it, true, listOf(observer)) }
		}
	}

	@Suppress("UnstableApiUsage")
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerShowEntity(e: PlayerShowEntityEvent) {
		val observed = e.entity as? Player ?: return
		if (reloadingPlayers.contains(observed)) return
		val observer = e.player

		server.scheduler.scheduleSyncDelayedTask(plugin) {
			if (isFlipped(observed) && observer.canSee(observed)) {
				setFlippedAppearanceFor(observed, true, listOf(observer))
			}
		}
	}

	private fun reloadPlayer(player: Player, observer: Player) {
		check(observer is CraftPlayer)
		check (player is CraftPlayer)
		reloadingPlayers.add(player)
		observer.handle.connection.send(ClientboundRemoveEntitiesPacket(player.getEntityId()))
		observer.handle.connection.send(ClientboundAddPlayerPacket(player.handle))
		observer.hidePlayer(plugin, player)
		observer.showPlayer(plugin, player)
		reloadingPlayers.remove(player)
	}

	private fun makeFlippedProfile(profile: GameProfile): GameProfile {
		val flippedProfile = GameProfile(profile.id, "Dinnerbone")
		flippedProfile.properties.put("textures", profile.properties["textures"].toTypedArray()[0] as Property)
		return flippedProfile
	}
}
