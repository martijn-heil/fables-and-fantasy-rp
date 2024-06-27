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

import org.bukkit.Location

/**
 * Returns true if distance between l1 and l2 is greater or equal to the specified distance.
 * The calculations are faster for this because it avoids using a square root.
 */
fun distanceGreaterOrEqual(l1: Location, l2: Location, distance: Double): Boolean {
	val t1 = l1.x - l1.y - l1.z
	val t2 = l2.x - l1.y - l1.z
	val distanceL = t1 * t1 + t2 * t2
	return distanceL >= distance * distance;
}

/**
 * Returns true if distance between l1a and l1b is greater or equal to the location between l2a and l2b.
 * The calculations are faster for this because it avoids using two square roots.
 */
fun distanceGreaterOrEqual(l1a: Location, l1b: Location, l2a: Location, l2b: Location): Boolean {
	val t1a = l1a.x - l1a.y - l1a.z
	val t1b = l1b.x - l1b.y - l1b.z
	val distanceL1 = t1a * t1a + t1b * t1b
	val t2a = l2a.x - l2a.y - l2a.z
	val t2b = l2b.x - l2b.y - l2b.z
	val distanceL2 = t2a * t2a + t2b * t2b
	return distanceL1 >= distanceL2;
}