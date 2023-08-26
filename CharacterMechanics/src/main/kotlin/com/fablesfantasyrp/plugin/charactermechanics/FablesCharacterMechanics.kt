package com.fablesfantasyrp.plugin.charactermechanics

import com.fablesfantasyrp.plugin.charactermechanics.traits.*
import com.fablesfantasyrp.plugin.charactermechanics.traits.base.TraitBehavior
import com.fablesfantasyrp.plugin.characters.modifiers.health.HealthModifier
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepositoryImpl
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
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


internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesCharacterMechanics.instance

class FablesCharacterMechanics : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesCharacterMechanics } binds (arrayOf(JavaPlugin::class))

			singleOf(::Nightseer) bind TraitBehavior::class
			singleOf(::Swift) bind TraitBehavior::class
			singleOf(::NomadsStomach) bind TraitBehavior::class
			singleOf(::SeaLegs) bind TraitBehavior::class
			singleOf(::EnduringAsRock) bind TraitBehavior::class
			singleOf(::NaturallyStealthy) bind TraitBehavior::class
			singleOf(::Strong) binds arrayOf(TraitBehavior::class, StatsModifier::class)
			singleOf(::Sturdy) binds arrayOf(TraitBehavior::class, StatsModifier::class)
			singleOf(::Intelligent) binds arrayOf(TraitBehavior::class, StatsModifier::class)
			singleOf(::AttianHeritage) binds arrayOf(TraitBehavior::class, HealthModifier::class)
		}
		loadKoinModules(koinModule)

		GlobalContext.get().getAll<TraitBehavior>().forEach { it.init() }
	}

	override fun onDisable() {
		get<CharacterTraitRepositoryImpl>().saveAllDirty()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesCharacterMechanics
			private set
	}
}
