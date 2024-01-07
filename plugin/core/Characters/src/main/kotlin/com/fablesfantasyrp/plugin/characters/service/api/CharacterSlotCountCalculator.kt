package com.fablesfantasyrp.plugin.characters.service.api

import org.bukkit.entity.Player

interface CharacterSlotCountCalculator {
	fun getCharacterSlots(player: Player): Int
}
