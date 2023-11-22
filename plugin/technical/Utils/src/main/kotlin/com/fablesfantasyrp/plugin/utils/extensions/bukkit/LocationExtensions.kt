package com.fablesfantasyrp.plugin.utils.extensions.bukkit

import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

data class ChunkCoordinates(val x: Int, val z: Int)

data class ColumnIdentifier(val world: UUID, val x: Int, val z: Int) {
	fun getChunkCoordinates() = ChunkCoordinates(x shr 4, z shr 4)
}

data class BlockCoordinates(val x: Int, val y: Int, val z: Int) {
	fun withWorld(world: UUID) = BlockIdentifier(world, x, y, z)
}
data class BlockIdentifier(val world: UUID, val x: Int, val y: Int, val z: Int) {
	fun toBlockCoordinates() = BlockCoordinates(x, y, z)
	fun toLocation() = Location(Bukkit.getWorld(world)!!, x.toDouble(), y.toDouble(), z.toDouble())
}

fun Location.humanReadable() = "${blockX},${blockY},${blockZ},${world.name}"

fun Location.toBlockIdentifier() = BlockIdentifier(this.world.uid, this.blockX, this.blockY, this.blockZ)

fun Location.distanceSafe(to: Location): Double {
	if (this.world != to.world) return Double.MAX_VALUE
	return this.distance(to)
}

fun ColumnIdentifier.groundLevel(): Location {
	val world = Bukkit.getServer().getWorld(world)!!
	return world.getHighestBlockAt(x, z).location.add(0.0, 1.0, 0.0)
}
