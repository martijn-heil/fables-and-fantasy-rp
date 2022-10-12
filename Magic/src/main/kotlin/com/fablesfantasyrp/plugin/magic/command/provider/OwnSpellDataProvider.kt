package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellData
import com.fablesfantasyrp.plugin.magic.mageRepository
import com.fablesfantasyrp.plugin.magic.spellRepository
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.entity.Player

class OwnSpellDataProvider(private val spellProvider: Provider<SimpleSpellData>) : Provider<SimpleSpellData> {

	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): SimpleSpellData {
		val sender = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
		val character = sender.currentPlayerCharacter ?: throw ArgumentParseException("You are not currently in character.")
		val mage = mageRepository.forPlayerCharacter(character) ?: throw ArgumentParseException("You are not a mage.")
		val spell = spellProvider.get(arguments, modifiers) ?: throw ArgumentParseException("Spell not found.")
		if (!mage.spells.contains(spell)) throw ArgumentParseException("You don't have access to this spell.")
		return spell
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		val sender = locals.get("sender") as? Player ?: return emptyList()
		val character = sender.currentPlayerCharacter ?: return emptyList()
		val mage = mageRepository.forPlayerCharacter(character) ?: return emptyList()

		return spellRepository.all()
				.asSequence()
				.filter { mage.spells.contains(it) }
				.filter { it.id.startsWith(prefix) }
				.map { it.id }
				.toList()
	}
}
