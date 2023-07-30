package com.fablesfantasyrp.plugin.charactertraits

import com.fablesfantasyrp.plugin.charactertraits.dal.h2.H2CharacterTraitDataRepository
import com.fablesfantasyrp.plugin.charactertraits.dal.repository.CharacterTraitDataRepository
import com.fablesfantasyrp.plugin.charactertraits.domain.mapper.CharacterTraitMapper
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepositoryImpl
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.ChatColor.*
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} CHARACTER TRAITS ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesCharacterTraits.instance

class FablesCharacterTraits : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_CHARACTER_TRAITS", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesCharacterTraits } binds (arrayOf(JavaPlugin::class))

			single { H2CharacterTraitDataRepository(fablesDatabase) } bind CharacterTraitDataRepository::class
			singleOf(::CharacterTraitMapper)
			singleOf(::CharacterTraitRepositoryImpl) bind CharacterTraitRepository::class
		}
		loadKoinModules(koinModule)

		server.scheduler.scheduleSyncRepeatingTask(this, {
			get<CharacterTraitRepositoryImpl>().saveAllDirty()
		}, 0L, 300L)
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
		get<CharacterTraitRepositoryImpl>().saveAllDirty()
	}

	companion object {
		lateinit var instance: FablesCharacterTraits
			private set
	}
}
