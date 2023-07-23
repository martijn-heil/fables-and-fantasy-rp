package com.fablesfantasyrp.plugin.alternatemechanics

import com.fablesfantasyrp.plugin.alternatemechanics.mechanic.*
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.ChatColor.*
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} ALTERNATE MECHANICS ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesAlternateMechanics.instance

class FablesAlternateMechanics : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesAlternateMechanics } binds (arrayOf(JavaPlugin::class))

			singleOf(::HorseJumpStrength) bind Mechanic::class
			singleOf(::LeadBreakSound) bind Mechanic::class
			singleOf(::NoGoldenApples) bind Mechanic::class
			singleOf(::NoHorseAi) bind Mechanic::class
			singleOf(::NoLeashingWhileInVehicle) bind Mechanic::class
			singleOf(::NoTotemOfUndying) bind Mechanic::class
		}
		loadKoinModules(koinModule)

		GlobalContext.get().getAll<Mechanic>().forEach { it.init() }
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesAlternateMechanics
			private set
	}
}
