package com.fablesfantasyrp.plugin.profile.command.provider

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.Provider
import org.bukkit.entity.Player

class ProfileModule(private val profiles: EntityProfileRepository,
					private val profileManager: ProfileManager,
					private val senderProvider: Provider<Player>) : AbstractModule() {
	override fun configure() {
		bind(Profile::class.java).toProvider(ProfileProvider(profiles, profileManager))
		bind(Profile::class.java).annotatedWith(Sender::class.java)
				.toProvider(ProfileSenderProvider(profileManager, senderProvider))
	}
}
