package com.fablesfantasyrp.plugin.playerinstance.command.provider

import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.sk89q.intake.argument.ArgumentException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.ProvisionException
import org.bukkit.entity.Player

class PlayerInstanceSenderProvider(	private val playerInstanceManager: PlayerInstanceManager,
									private val senderProvider: Provider<Player>) : Provider<PlayerInstance> {
	override fun isProvided(): Boolean = true

	@Throws(ArgumentException::class, ProvisionException::class)
	override fun get(commandArgs: CommandArgs, modifiers: List<Annotation>): PlayerInstance {
		val sender: Player = senderProvider.get(commandArgs, modifiers)!!
		return playerInstanceManager.getCurrentForPlayer(sender)
				?: throw ProvisionException("You are not a player instance")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
