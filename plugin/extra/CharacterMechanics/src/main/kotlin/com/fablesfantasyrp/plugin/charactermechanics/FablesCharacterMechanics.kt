/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.charactermechanics

import com.fablesfantasyrp.plugin.charactermechanics.racial.base.RaceBehavior
import com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani.SylvaniAntlers
import com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani.SylvaniBerries
import com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani.SylvaniNaturalHabitat
import com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani.SylvaniSkogPet
import com.fablesfantasyrp.plugin.charactermechanics.traits.*
import com.fablesfantasyrp.plugin.charactermechanics.traits.base.TraitBehavior
import com.fablesfantasyrp.plugin.characters.modifiers.health.HealthModifier
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
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
			singleOf(::TooAngryToDie) bind TraitBehavior::class
			singleOf(::SpidahRidah) bind TraitBehavior::class
			singleOf(::Strong) binds arrayOf(TraitBehavior::class, StatsModifier::class)
			singleOf(::Sturdy) binds arrayOf(TraitBehavior::class, StatsModifier::class)
			singleOf(::Intelligent) binds arrayOf(TraitBehavior::class, StatsModifier::class)
			singleOf(::Blind) binds arrayOf(TraitBehavior::class, StatsModifier::class)
			singleOf(::AttianHeritage) binds arrayOf(TraitBehavior::class, HealthModifier::class)

			singleOf(::SylvaniSkogPet) bind RaceBehavior::class
			singleOf(::SylvaniAntlers) bind RaceBehavior::class
			singleOf(::SylvaniBerries) bind RaceBehavior::class
			singleOf(::SylvaniNaturalHabitat) binds arrayOf(RaceBehavior::class, StatsModifier::class)
		}
		loadKoinModules(koinModule)

		GlobalContext.get().getAll<TraitBehavior>().forEach { it.init() }
		GlobalContext.get().getAll<RaceBehavior>().forEach { it.init() }
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesCharacterMechanics
			private set
	}
}
