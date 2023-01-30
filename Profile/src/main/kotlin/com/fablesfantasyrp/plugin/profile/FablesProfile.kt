package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.profile.command.Commands
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepositoryImpl
import com.fablesfantasyrp.plugin.profile.data.persistent.H2ProfileRepository
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.gitlab.martijn_heil.nincommands.common.bukkit.unregisterCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicePriority

internal val SYSPREFIX = "${GOLD}${BOLD}[${LIGHT_PURPLE}${BOLD} PROFILE ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesProfile.instance

lateinit var profileManager: ProfileManager
lateinit var profiles: EntityProfileRepository
	private set

class FablesProfile : SuspendingJavaPlugin() {
	private lateinit var commands: Collection<Command>


	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_PROFILE", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val profilesImpl = EntityProfileRepositoryImpl(H2ProfileRepository(server, fablesDatabase))
		profilesImpl.init()
		profiles = profilesImpl
		server.servicesManager.register(EntityProfileRepository::class.java, profiles, this, ServicePriority.Normal)

		profileManager = ProfileManagerImpl(server)
		Services.register(ProfileManager::class, profileManager, this, ServicePriority.Normal)

		val prompter = SimpleProfilePrompter(this)
		Services.register(ProfilePrompter::class, prompter, this, ServicePriority.Low)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(ProfileModule(profiles, profileManager, BukkitSenderProvider(Player::class.java)))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("profile", "prof", "p").registerMethods(Commands.CommandProfile(profiles, profileManager))
		rootDispatcherNode.registerMethods(Commands())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(ProfileListener(this, profiles, profileManager), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesProfile
	}
}
