package com.fablesfantasyrp.plugin.tools.command.provider

import com.sk89q.intake.parametric.AbstractModule

class ToolsModule : AbstractModule() {
	override fun configure() {
		bind(MinecraftTime::class.java).toProvider(MinecraftTimeProvider())
	}
}
