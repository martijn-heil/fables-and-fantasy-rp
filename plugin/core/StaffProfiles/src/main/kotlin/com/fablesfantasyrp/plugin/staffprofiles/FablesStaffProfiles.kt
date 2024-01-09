package com.fablesfantasyrp.plugin.staffprofiles

import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.staffprofiles.dal.h2.H2StaffProfileDataRepository
import com.fablesfantasyrp.plugin.staffprofiles.dal.repository.StaffProfileDataRepository
import com.fablesfantasyrp.plugin.staffprofiles.domain.repository.StaffProfileRepository
import com.fablesfantasyrp.plugin.staffprofiles.domain.repository.StaffProfileRepositoryImpl
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module


internal val PLUGIN get() = FablesStaffProfiles.instance

class FablesStaffProfiles : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_STAFFPROFILES", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesStaffProfiles } binds(arrayOf(JavaPlugin::class))

			singleOf(::H2StaffProfileDataRepository) bind StaffProfileDataRepository::class
			singleOf(::StaffProfileRepositoryImpl) bind StaffProfileRepository::class
			single { StaffProfilesListener(get(), get(), get(), get(), getOrNull()) }

			single {
				var worldBoundProfilesHook: WorldBoundProfilesHook? = null

				if (server.pluginManager.isPluginEnabled("FablesWorldBoundProfiles")) {
					worldBoundProfilesHook = com.fablesfantasyrp.plugin.staffprofiles.WorldBoundProfilesHookImpl()
				} else {
					this@FablesStaffProfiles.logger.warning("Could not find FablesWorldBoundProfiles, integration will not be activated.")
				}

				worldBoundProfilesHook
			}
		}
		loadKoinModules(koinModule)

		val staffProfiles = get<StaffProfileRepositoryImpl>()
		frunBlocking { staffProfiles.init() }

		val worldBoundProfilesHook: WorldBoundProfilesHook? = GlobalContext.get().getOrNull()

		if (worldBoundProfilesHook != null) {
			frunBlocking { staffProfiles.all() }.forEach { worldBoundProfilesHook.allowToFlatroom(it) }
		}

		server.pluginManager.registerEvents(get<StaffProfilesListener>(), this)
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesStaffProfiles
			private set
	}
}
