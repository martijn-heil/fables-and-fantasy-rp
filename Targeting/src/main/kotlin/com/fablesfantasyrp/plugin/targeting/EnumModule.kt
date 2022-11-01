package com.fablesfantasyrp.plugin.targeting

import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.provider.EnumProvider

class EnumModule : AbstractModule() {
	override fun configure() {
		bind(EnumOne::class.java).toProvider(EnumProvider(EnumOne::class.java))
		bind(EnumTwo::class.java).toProvider(EnumProvider(EnumTwo::class.java))
	}
}
