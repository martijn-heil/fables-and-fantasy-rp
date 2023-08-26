package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.entity.Player

class MageSenderProvider(private val profileManager: ProfileManager,
						 private val characters: CharacterRepository,
						 private val mages: MageRepository) : Provider<Mage> {
	override fun isProvided(): Boolean = true

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Mage {
		val player = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
		val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) }
			?: throw ArgumentParseException("You are not currently in character.")
		return mages.forCharacter(character)
				?: throw ArgumentParseException("You have to be a mage to execute this command.")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
