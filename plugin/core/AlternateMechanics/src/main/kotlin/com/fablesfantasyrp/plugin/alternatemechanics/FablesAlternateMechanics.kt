package com.fablesfantasyrp.plugin.alternatemechanics

import com.fablesfantasyrp.plugin.alternatemechanics.mechanic.*
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import org.bukkit.Server
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


internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesAlternateMechanics.instance

class FablesAlternateMechanics : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesAlternateMechanics } binds (arrayOf(JavaPlugin::class))

			single { WorldGuard.getInstance() }
			single { get<Server>().pluginManager.getPlugin("WorldGuard") as WorldGuardPlugin }
			single { get<WorldGuard>().platform.regionContainer }

			singleOf(::HorseJumpStrength) bind Mechanic::class
			singleOf(::HorseTrampling) bind Mechanic::class
			singleOf(::LeadBreakSound) bind Mechanic::class
			singleOf(::NoGoldenApples) bind Mechanic::class
			singleOf(::NoHorseAi) bind Mechanic::class
			singleOf(::NoLeashingWhileInVehicle) bind Mechanic::class
			singleOf(::NoTotemOfUndying) bind Mechanic::class
			singleOf(::NoEnchanting) bind Mechanic::class
			singleOf(::FarmReplant) bind Mechanic::class
			singleOf(::InvisibleItemFrames) bind Mechanic::class
			singleOf(::RestrictedDualWielding) bind Mechanic::class
			singleOf(::EverythingIronLevel) bind Mechanic::class
			singleOf(::DisableCraftingRecipes) bind Mechanic::class
			singleOf(::NoCropTrample) bind Mechanic::class
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
