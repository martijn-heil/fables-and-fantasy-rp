package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.authorizer.SpellAuthorizer
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderProvider
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import org.bukkit.entity.Player

class OwnSpellDataProvider(private val spellProvider: Provider<SpellData>,
						   private val profileManager: ProfileManager,
						   private val characters: CharacterRepository,
						   private val spellAuthorizer: SpellAuthorizer) : Provider<SpellData> {

	override val isProvided: Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): SpellData {
		val sender = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
		val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
				?: throw ArgumentParseException("You are not currently in character.")
		val spell = spellProvider.get(arguments, modifiers)
		if (!spellAuthorizer.hasSpell(character, spell)) throw ArgumentParseException("You don't have access to this spell.")
		return spell
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		val sender = locals.get("sender") as? Player ?: return emptyList()
		val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
				?: return emptyList()

		return spellAuthorizer.getSpells(character)
			.asSequence()
			.filter { it.id.startsWith(prefix) }
			.map { it.id }
			.toList()
	}
}
