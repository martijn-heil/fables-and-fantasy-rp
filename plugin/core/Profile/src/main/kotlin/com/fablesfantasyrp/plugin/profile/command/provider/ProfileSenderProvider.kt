package com.fablesfantasyrp.plugin.profile.command.provider

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.caturix.argument.ArgumentException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.parametric.ProvisionException
import org.bukkit.entity.Player

class ProfileSenderProvider(private val profileManager: ProfileManager,
							private val senderProvider: Provider<Player>) : Provider<Profile> {
	override val isProvided: Boolean = true

	@Throws(ArgumentException::class, ProvisionException::class)
	override suspend fun get(commandArgs: CommandArgs, modifiers: List<Annotation>): Profile {
		val sender: Player = senderProvider.get(commandArgs, modifiers)!!
		return profileManager.getCurrentForPlayer(sender)
				?: throw ProvisionException("You are not a profile")
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
