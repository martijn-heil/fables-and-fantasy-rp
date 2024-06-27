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
