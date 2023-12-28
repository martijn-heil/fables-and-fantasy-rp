package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.authorizer.MagicTypeAuthorizer
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicType
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderProvider
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.parametric.provider.EnumProvider
import org.bukkit.entity.Player

class OwnMagicTypeProvider(private val profileManager: ProfileManager,
						   private val characters: CharacterRepository,
						   private val enumProvider: EnumProvider<MagicType>,
						   private val magicTypeAuthorizer: MagicTypeAuthorizer) : Provider<MagicType> {

	override val isProvided: Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): MagicType {
		val sender = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
		val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
				?: throw ArgumentParseException("You are not currently in character.")

		val magicType = enumProvider.get(arguments, modifiers)

		if (!magicTypeAuthorizer.getMagicTypes(character).contains(magicType))
			throw ArgumentParseException("You don't have access to this element.")

		return magicType
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		val sender = locals.get("sender") as? Player ?: return emptyList()
		val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
				?: return emptyList()

		return magicTypeAuthorizer.getMagicTypes(character)
			.map { it.name }
			.filter { it.startsWith(prefix) }
	}
}
