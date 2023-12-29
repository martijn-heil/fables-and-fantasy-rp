package com.fablesfantasyrp.plugin.profile.command.provider

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.parametric.AbstractModule
import com.fablesfantasyrp.caturix.parametric.Provider
import org.bukkit.Server
import org.bukkit.entity.Player

class ProfileModule(private val profiles: EntityProfileRepository,
					private val profileManager: ProfileManager,
					private val senderProvider: Provider<Player>,
					private val playerProvider: Provider<Player>,
					private val server: Server) : AbstractModule() {
	override fun configure() {
		bind(Profile::class.java).toProvider(ProfileProvider(profiles, profileManager, playerProvider, server))
		bind(Profile::class.java).annotatedWith(Sender::class.java)
				.toProvider(ProfileSenderProvider(profileManager, senderProvider))
	}
}
