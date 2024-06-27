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
package com.fablesfantasyrp.plugin.domain

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.*


val FABLES_ADMIN = Bukkit.getOfflinePlayer(UUID.fromString("bcdb5a59-269e-43df-914b-eed888597272"))
val SPAWN: Location get() = EDEN!!.spawnLocation.toCenterLocation()
val FLATROOM: World? get() = Bukkit.getWorld("flatroom")
val PLOTS: World? get() = Bukkit.getWorld("plots")
val EDEN: World? get() = Bukkit.getWorld("Eden")
val BACKROOMS: World? get() = Bukkit.getWorld("backrooms")

const val DISTANCE_WHISPER = 2U
const val DISTANCE_QUIET = 8u
const val DISTANCE_TALK = 15U
const val DISTANCE_SHOUT = 30U

const val NAMESPACE = "fablesfantasyrp"
