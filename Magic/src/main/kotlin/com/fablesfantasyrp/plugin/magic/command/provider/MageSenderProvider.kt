package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.mageRepository
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.entity.Player

class MageSenderProvider : Provider<Mage> {
	override fun isProvided(): Boolean = true

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Mage {
		val player = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
		val character = player.currentPlayerCharacter ?: throw ArgumentParseException("You are not currently in character.")
		return mageRepository.forPlayerCharacter(character)
				?: throw ArgumentParseException("You have to be a mage to execute this command.")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
