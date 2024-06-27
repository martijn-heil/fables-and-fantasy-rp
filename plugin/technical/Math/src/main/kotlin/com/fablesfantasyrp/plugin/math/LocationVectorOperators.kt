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
import org.bukkit.util.Vector

operator fun Location.plus(what: Location) = this.clone().add(what)!! // l1 + l2
operator fun Location.plus(what: Vector) = this.clone().add(what)!! // l + v
operator fun Location.minus(what: Location) = this.clone().subtract(what)!! // l1 - l2
operator fun Location.minus(what: Vector) = this.clone().subtract(what)!! // l - v
operator fun Location.times(what: Double) = this.clone().multiply(what)!! // l1 * a
operator fun Location.div(what: Double) = this.clone().multiply(1 / what)!! // l1 / a

operator fun Vector.plus(what: Vector) = this.clone().add(what)!! // v1 + v2
operator fun Vector.minus(what: Vector) = this.clone().subtract(what)!! // v1 - v2
operator fun Vector.times(what: Double) = this.clone().multiply(what)!! // v1 * a
operator fun Vector.div(what: Double) = this.clone().multiply(1 / what)!! // v1 / a