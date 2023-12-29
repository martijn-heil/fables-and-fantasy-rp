package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.frunBlocking
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderProvider
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import org.bukkit.entity.Player

class MageSenderProvider(private val profileManager: ProfileManager,
						 private val characters: CharacterRepository,
						 private val mages: MageRepository) : Provider<Mage> {
	override val isProvided: Boolean = true

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Mage {
		val player = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
		val character = profileManager.getCurrentForPlayer(player)?.let { frunBlocking { characters.forProfile(it) } }
			?: throw ArgumentParseException("You are not currently in character.")
		return mages.forCharacter(character)
				?: throw ArgumentParseException("You have to be a mage to execute this command.")
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
