package com.fablesfantasyrp.plugin.characters.modifiers.characterslots

import org.bukkit.entity.Player

interface CharacterSlotCountModifier {
	fun calculateModifier(player: Player): Int
}
