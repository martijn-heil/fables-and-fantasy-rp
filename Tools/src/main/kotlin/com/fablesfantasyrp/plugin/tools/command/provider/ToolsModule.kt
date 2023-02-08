package com.fablesfantasyrp.plugin.tools.command.provider

import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.provider.EnumProvider

class ToolsModule : AbstractModule() {
	override fun configure() {
		bind(MinecraftTime::class.java).toProvider(MinecraftTimeProvider())
		bind(Weather::class.java).toProvider(EnumProvider(Weather::class.java))
	}
}
