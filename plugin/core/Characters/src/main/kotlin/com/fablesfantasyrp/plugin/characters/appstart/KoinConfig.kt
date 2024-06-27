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
package com.fablesfantasyrp.plugin.characters.appstart

import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.plugin.characters.*
import com.fablesfantasyrp.plugin.characters.command.CharacterTraitCommand
import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.LegacyCommands
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.dal.h2.H2CharacterDataRepository
import com.fablesfantasyrp.plugin.characters.domain.mapper.CharacterMapper
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepositoryImpl
import com.fablesfantasyrp.plugin.characters.modifiers.characterslots.CharacterSlotCountModifier
import com.fablesfantasyrp.plugin.characters.modifiers.characterslots.PremiumRankCharacterSlotCountModifier
import com.fablesfantasyrp.plugin.characters.modifiers.stats.RaceStatsModifier
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.characters.service.api.CharacterCardGenerator
import com.fablesfantasyrp.plugin.characters.service.api.CharacterSlotCountCalculator
import com.fablesfantasyrp.plugin.characters.service.api.CreatureSizeCalculator
import com.fablesfantasyrp.plugin.characters.service.implementation.CharacterCardGeneratorImpl
import com.fablesfantasyrp.plugin.characters.service.implementation.CharacterSlotCountCalculatorImpl
import com.fablesfantasyrp.plugin.characters.service.implementation.CreatureSizeCalculatorImpl
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.koin.BaseKoinConfig
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal class KoinConfig(private val plugin: Plugin) : BaseKoinConfig() {
	override val koinModule = module(true) {
		single <Plugin> { plugin } binds(arrayOf(JavaPlugin::class))

		single {
			val h2CharacterRepository = H2CharacterDataRepository(get(), get(), get())
			val mapper = CharacterMapper(h2CharacterRepository, get())
			CharacterRepositoryImpl(mapper, get())
		} bind CharacterRepository::class

		singleOf(::CharacterAuthorizerImpl) bind CharacterAuthorizer::class
		singleOf(::CharacterCardGeneratorImpl) bind CharacterCardGenerator::class
		singleOf(::CharacterSlotCountCalculatorImpl) bind CharacterSlotCountCalculator::class

		factoryOf(::RaceStatsModifier) bind StatsModifier::class
		factoryOf(::CreatureSizeCalculatorImpl) bind CreatureSizeCalculator::class
		factoryOf(::PremiumRankCharacterSlotCountModifier) bind CharacterSlotCountModifier::class

		factory { CharacterModule(get(), get(), get(), get<Provider<Profile>>(named("Profile"))) }
		singleOf(::CharactersListener)
		singleOf(::CharactersLiveMigrationListener)
		singleOf(::CharacterCreationListener)

		singleOf(::Commands)
		single { get<Commands>().Characters() }
		single { get<Commands.Characters>().Change() }
		single { get<Commands.Characters>().Stats() }

		singleOf(::CharacterTraitCommand)
		single { get<CharacterTraitCommand>().CharacterTraitCommand() }

		singleOf(::LegacyCommands)

		singleOf(::CommandConfig)
	}
}
