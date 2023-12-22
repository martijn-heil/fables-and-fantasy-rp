package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.authorizer.MagicTypeAuthorizer
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicType
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.provider.EnumProvider
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player

class OwnMagicTypeProvider(private val profileManager: ProfileManager,
						   private val characters: CharacterRepository,
						   private val magicTypeAuthorizer: MagicTypeAuthorizer) : EnumProvider<MagicType>(MagicType::class.java) {

	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): MagicType {
		val sender = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
		val character = profileManager.getCurrentForPlayer(sender)?.let { runBlocking { characters.forProfile(it) } }
				?: throw ArgumentParseException("You are not currently in character.")

		val magicType = super.get(arguments, modifiers)!!

		if (!magicTypeAuthorizer.getMagicTypes(character).contains(magicType))
			throw ArgumentParseException("You don't have access to this element.")

		return magicType
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		val sender = locals.get("sender") as? Player ?: return emptyList()
		val character = profileManager.getCurrentForPlayer(sender)?.let { runBlocking { characters.forProfile(it) } }
				?: return emptyList()

		return magicTypeAuthorizer.getMagicTypes(character)
			.map { it.name }
			.filter { it.startsWith(prefix) }
	}
}
