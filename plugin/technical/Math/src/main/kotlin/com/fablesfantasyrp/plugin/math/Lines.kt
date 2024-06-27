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
package com.fablesfantasyrp.plugin.math

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.util.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin


/**
 * A set of utilities that can draw lines between two locations - see drawLine().
 * Additionally, can draw a line, apply f(x), then rotate the output - see drawLineWithFunction(f).
 */

/**
 * Draw line from A to B, using an RGB colored particle, with offset spacing.
 */
fun drawLine(color: Color = Color.FUCHSIA, A: Location, B: Location, offset: Double = 0.25) {

	check(A.world != null)
	check(B.world != null)
	check(A.world == B.world)

	var dir = (B - A).toVector()
	val length = dir.length()
	dir.normalize()
	dir *= offset

	val dustOptions = DustOptions(color, 1.0f)
	var i = 1.0

	while (i <= length) {
		A.world!!.spawnParticle(Particle.REDSTONE, A + (dir * i), 1, dustOptions)
		i += offset
	}

}

/**
 * Draw line from A to B, using an RGB colored particle, with offset spacing.
 * The position of every particle will be offset by Vector f(l) applied to Location l.
 * Additionally, the line will be rotated, so that the graph of f(l) is along line AB.
 */
fun drawLineUsingFunction(color: Color = Color.FUCHSIA, A: Location, B: Location, offset: Double = 0.25,
					  f: (l: Location, i: Double) -> Vector = wave()) {

	check(A.world != null)
	check(B.world != null)
	check(A.world == B.world)

	var dir = (B - A).toVector()
	val length = dir.length()
	dir.normalize()
	dir *= offset

	val sub = (B - A)
	val alpha = sub.pitch * (PI / 180)
	val beta = sub.yaw * (PI / 180)

	val pSinB = sin(beta) * alpha
	val pCosB = cos(beta) * alpha

	val dustOptions = DustOptions(color, 1.0f)
	var i = 1.0

	val world = A.world!!

	while (i <= length) {
		val loc = A + (dir * i)
		val vec = f(loc, i).rotateAroundX(pCosB).rotateAroundZ(pSinB)
		world.spawnParticle(Particle.REDSTONE, loc + vec, 1, dustOptions)
		i += offset
	}

}

/**
 * Predefined functions f(l) which you can apply on drawLineWithFunction.
 */

/**
 * Draws a cosine graph with a period, amplitude, phase, and offset.
 * A higher period means more common undulations in the graph.
 * A higher amplitude means the graph's height will be more extreme.
 * The phase will simply shift the entire graph along its length by some amount.
 * The offset will raise or lower the entire graph vertically.
 */
fun wave(period: Double = 1.0, amplitude: Double = 1.0, phase: Double = 0.0, offset: Double = 0.0): (Location, Double) -> Vector {
	return { _: Location, i: Double ->
		Vector(0.0f, (amplitude * cos(i * period + phase) + offset).toFloat(), 0.0f) }
}

/**
 * Draws a cosine graph with a period, amplitude, phase, and offset, multiplied by a growing scale factor.
 * A higher period means more common undulations in the graph.
 * A higher amplitude means the graph's height will be more extreme.
 * The phase will simply shift the entire graph along its length by some amount.
 * The offset will raise or lower the entire graph vertically.
 * A scale factor greater than 0 will make the wave grow over time.
 * A scale factor equal to 0 is equivalent to wave().
 * A scale factor less than 0 will make the wave grow, but shift the phase.
 */
fun waveGrow(period: Double = 1.0, amplitude: Double = 1.0, phase: Double = 0.0, offset: Double = 0.0,
			 scale: Double = 0.0): (Location, Double) -> Vector {
	return { _: Location, i: Double ->
		Vector(0.0f, ((amplitude * cos(i * period + phase) + offset) * (scale * i)).toFloat(), 0.0f) }
}

/**
 * Draws a cosine graph with a period, amplitude, phase, and offset, multiplied by a growing logarithmic scale factor.
 * A higher period means more common undulations in the graph.
 * A higher amplitude means the graph's height will be more extreme.
 * The phase will simply shift the entire graph along its length by some amount.
 * The offset will raise or lower the entire graph vertically.
 * The scale factor will make the ease slower and the graph amplitude more extreme.
 */
fun waveGrowEaseInOut(period: Double = 1.0, amplitude: Double = 1.0, phase: Double = 0.0, offset: Double = 0.0,
			 scale: Double = 0.0): (Location, Double) -> Vector {
	return { _: Location, i: Double ->
		Vector(0.0f, ((amplitude * cos(i * period + phase) + offset) * (scale * ln(i))).toFloat(), 0.0f) }
}

/**
 * Draws a helix, additionally defined by several properties.
 * A higher period means more common undulations in the graph.
 * A higher amplitude means the graph's height will be more extreme.
 * The phase will simply shift the entire graph along its length by some amount.
 * The offset will raise or lower the entire graph vertically.
 */
fun helix(period: Double = 1.0, amplitude: Double = 1.0, phase: Double = 0.0, offset: Double = 0.0): (Location, Double) -> Vector {
	return { l: Location, i: Double ->
		val yaw = l.yaw * (PI / 180);
		Vector(((amplitude * sin(i * period + phase) + offset) * cos(yaw)).toFloat(),
				(amplitude * cos(i * period + phase) + offset).toFloat(),
				((amplitude * sin(i * period + phase) + offset) * sin(yaw)).toFloat())
	}
}