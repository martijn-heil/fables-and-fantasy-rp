package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.MageAbilities
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.frunBlocking
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.entity.Player

class MageAbilityProvider(private val profileManager: ProfileManager,
						  private val characters: CharacterRepository,
						  private val mages: MageRepository) : Provider<MageAbility> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): MageAbility {
		val ability = MageAbilities.forId(arguments.next()) ?: throw ArgumentParseException("Ability not found.")
		if (modifiers.find { it is OwnAbility } != null) {
			val player = BukkitSenderProvider(Player::class.java).get(arguments, modifiers)!!
			val character = profileManager.getCurrentForPlayer(player)?.let { frunBlocking { characters.forProfile(it) } }
				?: throw ArgumentParseException("You are not in character.")
			val mage = mages.forCharacter(character) ?: throw ArgumentParseException("You are not a mage.")
			if (mage.magicPath != ability.magicPath && mage.magicPath.basePath != ability.magicPath)
				throw ArgumentParseException("You don't have access to this ability.")
		}
		return ability
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		if (modifiers.find { it is OwnAbility } != null) {
			val player = locals.get("sender") as? Player ?: return emptyList()
			val character = profileManager.getCurrentForPlayer(player)?.let { frunBlocking { characters.forProfile(it) } }
					?: return emptyList()
			val mage = mages.forCharacter(character) ?: return emptyList()
			return MageAbilities.forPath(mage.magicPath).asSequence()
					.map { it.id }
				.filter { it.startsWith(prefix) }
					.toList()
		} else {
			return MageAbilities.all.asSequence()
					.map { it.id }
					.filter { it.startsWith(prefix) }
					.toList()
		}
	}
}
