package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.magic.MageAbilities
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.mageRepository
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.entity.Player

class MageAbilityProvider : Provider<MageAbility> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): MageAbility {
		val ability = MageAbilities.forId(arguments.next()) ?: throw ArgumentParseException("Ability not found.")
		if (modifiers.find { it is OwnAbility } != null) {
			val player = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
			val character = player.currentPlayerCharacter ?: throw ArgumentParseException("You are not in character.")
			val mage = mageRepository.forPlayerCharacter(character) ?: throw ArgumentParseException("You are not a mage.")
			if (mage.magicPath != ability.magicPath && mage.magicPath.basePath != ability.magicPath)
				throw ArgumentParseException("You don't have access to this ability.")
		}
		return ability
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return MageAbilities.all.asSequence()
				.map { it.id }
				.filter { it.startsWith(prefix) }
				.toList()
	}
}
