package com.fablesfantasyrp.plugin.profile.command.provider

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.sk89q.intake.argument.ArgumentException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.ProvisionException
import org.bukkit.entity.Player

class ProfileSenderProvider(private val profileManager: ProfileManager,
							private val senderProvider: Provider<Player>) : Provider<Profile> {
	override fun isProvided(): Boolean = true

	@Throws(ArgumentException::class, ProvisionException::class)
	override fun get(commandArgs: CommandArgs, modifiers: List<Annotation>): Profile {
		val sender: Player = senderProvider.get(commandArgs, modifiers)!!
		return profileManager.getCurrentForPlayer(sender)
				?: throw ProvisionException("You are not a profile")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
