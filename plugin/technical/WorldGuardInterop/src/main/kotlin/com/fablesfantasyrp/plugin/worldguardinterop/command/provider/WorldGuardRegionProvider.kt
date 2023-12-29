package com.fablesfantasyrp.plugin.worldguardinterop.command.provider

import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Server

class WorldGuardRegionProvider(private val server: Server, private val regionContainer: RegionContainer) : Provider<WorldGuardRegion> {
	override val isProvided = false

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): WorldGuardRegion {
		val descriptor = arguments.next().split(",")
		val regionName = descriptor.getOrNull(0) ?: throw ArgumentParseException("Missing region name")
		val worldName = descriptor.getOrNull(1) ?: throw ArgumentParseException("Missing world name")
		val world = server.getWorld(worldName) ?: throw ArgumentParseException("Invalid world '$worldName'")
		val regions = regionContainer.get(BukkitAdapter.adapt(world))
				?: throw ArgumentParseException("World '$worldName' does not have any regions")
		return WorldGuardRegion(world, regions.getRegion(regionName)
				?: throw ArgumentParseException("Unknown region '$regionName' in world '$worldName'"))
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return server.worlds.asSequence()
				.mapNotNull { world -> regionContainer.get(BukkitAdapter.adapt(world))?.let { Pair(world, it) } }
				.map { Pair(it.first, it.second.regions.values) }
				.map { pair -> pair.second.map { "${it.id},${pair.first.name}" } }
				.flatten()
				.filter { it.startsWith(prefix) }
				.toList()
	}
}
