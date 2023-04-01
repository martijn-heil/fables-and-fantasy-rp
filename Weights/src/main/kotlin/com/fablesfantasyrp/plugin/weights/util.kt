package com.fablesfantasyrp.plugin.weights

import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

fun applyWeight(player: Player, weight: Int, cap: Int) {
	if (weight > cap) {
		val weakness = PotionEffect(PotionEffectType.WEAKNESS, 999999, 1, false, false)
		val slowness = PotionEffect(PotionEffectType.SLOW, 999999, 1, false, false)
		player.addPotionEffect(weakness)
		player.addPotionEffect(slowness)
	} else {
		player.removePotionEffect(PotionEffectType.WEAKNESS)
		player.removePotionEffect(PotionEffectType.SLOW)
	}
}
