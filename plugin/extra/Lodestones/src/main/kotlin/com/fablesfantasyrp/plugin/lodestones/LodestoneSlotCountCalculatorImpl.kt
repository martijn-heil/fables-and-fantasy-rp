package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.utils.getPermissionLevel
import org.bukkit.entity.Player

const val MAX_LODESTONE_SLOTS = 4

class LodestoneSlotCountCalculatorImpl : LodestoneSlotCountCalculator {
	override fun getLodestoneSlots(player: Player): Int
		= player.getPermissionLevel(Permission.Slots, MAX_LODESTONE_SLOTS)
}
