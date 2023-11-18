package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

interface CharacterCardGenerator {
	fun card(character: Character, observer: CommandSender? = null): Component
}
