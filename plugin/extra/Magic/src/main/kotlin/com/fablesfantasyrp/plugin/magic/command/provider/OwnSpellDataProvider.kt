package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.authorizer.SpellAuthorizer
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player

class OwnSpellDataProvider(private val spellProvider: Provider<SpellData>,
						   private val profileManager: ProfileManager,
						   private val characters: CharacterRepository,
						   private val mages: MageRepository,
						   private val spellAuthorizer: SpellAuthorizer) : Provider<SpellData> {

	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): SpellData {
		val sender = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
		val character = profileManager.getCurrentForPlayer(sender)?.let { runBlocking { characters.forProfile(it) } }
				?: throw ArgumentParseException("You are not currently in character.")
		val spell = spellProvider.get(arguments, modifiers) ?: throw ArgumentParseException("Spell not found.")
		if (!spellAuthorizer.hasSpell(character, spell)) throw ArgumentParseException("You don't have access to this spell.")
		return spell
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		val sender = locals.get("sender") as? Player ?: return emptyList()
		val character = profileManager.getCurrentForPlayer(sender)?.let { runBlocking { characters.forProfile(it) } }
				?: return emptyList()

		return spellAuthorizer.getSpells(character)
			.asSequence()
			.filter { it.id.startsWith(prefix) }
			.map { it.id }
			.toList()
	}
}
