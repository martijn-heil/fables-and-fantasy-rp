package com.fablesfantasyrp.plugin.charactertraits.command.provider

import com.fablesfantasyrp.plugin.charactertraits.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.sk89q.intake.parametric.AbstractModule

class CharacterTraitModule(private val traits: CharacterTraitRepository) : AbstractModule() {
	override fun configure() {
		bind(CharacterTrait::class.java).toProvider(CharacterTraitProvider(traits))
	}
}
