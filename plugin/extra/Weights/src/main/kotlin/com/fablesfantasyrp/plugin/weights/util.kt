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
package com.fablesfantasyrp.plugin.weights

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

fun applyWeight(player: Player, weight: Int, cap: Int) {
	if (player.gameMode != GameMode.SPECTATOR && player.gameMode != GameMode.CREATIVE && weight > cap) {
		if (!(player.hasPotionEffect(PotionEffectType.WEAKNESS) && player.hasPotionEffect(PotionEffectType.SLOW))) {
			player.sendMessage("$SYSPREFIX You're carrying too much equipment. Check your weights with /weights")
		}
		val weakness = PotionEffect(PotionEffectType.WEAKNESS, 999999, 1, false, false)
		val slowness = PotionEffect(PotionEffectType.SLOW, 999999, 1, false, false)
		player.addPotionEffect(weakness)
		player.addPotionEffect(slowness)
	} else {
		player.removePotionEffect(PotionEffectType.WEAKNESS)
		player.removePotionEffect(PotionEffectType.SLOW)
	}
}
