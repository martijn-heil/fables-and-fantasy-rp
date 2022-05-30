package com.fablesfantasyrp.plugin.rolls.command.provider

import com.fablesfantasyrp.plugin.characters.CharacterStatKind
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.provider.EnumProvider

class RollsCommandModule : AbstractModule() {
	override fun configure() {
		bind(CharacterStatKind::class.java).toProvider(EnumProvider(CharacterStatKind::class.java))
	}
}
