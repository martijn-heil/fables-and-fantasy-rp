package com.fablesfantasyrp.plugin.playerinstance.command.provider

import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.Provider
import org.bukkit.entity.Player

class PlayerInstanceModule(private val playerInstances: EntityPlayerInstanceRepository,
						   private val playerInstanceManager: PlayerInstanceManager,
						   private val senderProvider: Provider<Player>) : AbstractModule() {
	override fun configure() {
		bind(PlayerInstance::class.java).toProvider(PlayerInstanceProvider(playerInstances, playerInstanceManager))
		bind(PlayerInstance::class.java).annotatedWith(Sender::class.java)
				.toProvider(PlayerInstanceSenderProvider(playerInstanceManager, senderProvider))
	}
}
