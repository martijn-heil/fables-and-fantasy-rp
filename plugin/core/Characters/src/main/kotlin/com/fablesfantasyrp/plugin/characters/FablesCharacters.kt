package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.appstart.CommandConfig
import com.fablesfantasyrp.plugin.characters.appstart.KoinConfig
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepositoryImpl
import com.fablesfantasyrp.plugin.characters.web.WebHook
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal val SYSPREFIX = GLOBAL_SYSPREFIX

internal val PLUGIN get() = FablesCharacters.instance

class FablesCharacters : JavaPlugin(), KoinComponent {
	private lateinit var koinConfig: KoinConfig

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_CHARACTERS", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinConfig = KoinConfig(this)
		koinConfig.load()

		(get<CharacterRepository>() as CharacterRepositoryImpl).init()

		server.servicesManager.register(CharacterRepository::class.java, get(), this, ServicePriority.Normal)

		get<CommandConfig>().init()

		server.pluginManager.registerEvents(get<CharactersListener>(), this)
		server.pluginManager.registerEvents(get<CharactersLiveMigrationListener>(), this)
		server.pluginManager.registerEvents(get<CharacterCreationListener>(), this)

		if (server.pluginManager.isPluginEnabled("TAB") && server.pluginManager.isPluginEnabled("Denizen") ) {
			logger.info("Enabling TAB integration")
			com.fablesfantasyrp.plugin.characters.nametags.NameTagManager(get(), get()).start()
		}

		if (server.pluginManager.isPluginEnabled("FablesWeb")) {
			try {
				logger.info("Enabling FablesWeb integration")
				WebHook().start()
			} catch (ex: Exception) {
				ex.printStackTrace()
				logger.warning("An error occurred during setup of FablesWeb integration.")
			}
		}

		/*server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving characters..")
			get<CharacterRepositoryImpl>().saveAllDirty()
		}, 0, 6000)

		server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving character traits..")
			get<CharacterTraitRepositoryImpl>().saveAllDirty()
		}, 0, 6000)*/
	}

	override fun onDisable() {
		get<CommandConfig>().cleanup()
		get<CharacterRepositoryImpl>().saveAllDirty()
		koinConfig.unload()
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}
