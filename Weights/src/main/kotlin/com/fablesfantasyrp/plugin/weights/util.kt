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
