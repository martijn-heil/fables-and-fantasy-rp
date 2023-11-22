package com.fablesfantasyrp.plugin.lodestones

import org.bukkit.entity.Player

interface LodestoneSlotCountCalculator {
	fun getLodestoneSlots(player: Player): Int
}
