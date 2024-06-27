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
package com.fablesfantasyrp.plugin.utilsoffline

import net.quazar.offlinemanager.api.data.entity.IPlayerData
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.Inventory

private val OfflinePlayer.offlinePlayerData: IPlayerData
	get() = offlineManagerAPI.getPlayerData(uniqueId)

var OfflinePlayer.gameMode: GameMode
	get() = player?.gameMode ?: offlinePlayerData.gameMode
	set(value) {
		val p = player
		if (p != null) {
			p.gameMode = value
		} else {
			offlinePlayerData.gameMode = value
		}
	}

var OfflinePlayer.location: Location
	get() = player?.location ?: offlinePlayerData.location
	set(value) {
		val p = player
		if (p != null) {
			p.teleport(value)
		} else {
			offlinePlayerData.location = value
		}
	}

var OfflinePlayer.exhaustion: Float
	get() = player?.exhaustion ?: offlinePlayerData.exhaustion
	set(value) {
		val p = player
		if (p != null) {
			p.exhaustion = value
		} else {
			offlinePlayerData.exhaustion = value
		}
	}

var OfflinePlayer.exp: Float
	get() = player?.exp ?: offlinePlayerData.exp
	set(value) {
		val p = player
		if (p != null) {
			p.exp = value
		} else {
			offlinePlayerData.exp = value
		}
	}

var OfflinePlayer.foodLevel: Int
	get() = player?.foodLevel ?: offlinePlayerData.foodLevel
	set(value) {
		val p = player
		if (p != null) {
			p.foodLevel = value
		} else {
			offlinePlayerData.foodLevel = value
		}
	}

var OfflinePlayer.saturation: Float
	get() = player?.saturation ?: offlinePlayerData.saturation
	set(value) {
		val p = player
		if (p != null) {
			p.saturation = value
		} else {
			offlinePlayerData.saturation = value
		}
	}

var OfflinePlayer.health: Double
	get() = player?.health ?: offlinePlayerData.health.toDouble()
	set(value) {
		val p = player
		if (p != null) {
			p.health = value
		} else {
			offlinePlayerData.health = value.toFloat()
		}
	}

var OfflinePlayer.isInvulnerable: Boolean
	get() = player?.isInvulnerable ?: offlinePlayerData.isInvulnerable
	set(value) {
		val p = player
		if (p != null) {
			p.isInvulnerable = value
		} else {
			offlinePlayerData.isInvulnerable = value
		}
	}

val OfflinePlayer.offlineEnderChest: Inventory
	get() = offlinePlayerData.enderChest.enderChest

val OfflinePlayer.offlineInventory: Inventory
	get() = offlinePlayerData.inventory

// TODO add more
