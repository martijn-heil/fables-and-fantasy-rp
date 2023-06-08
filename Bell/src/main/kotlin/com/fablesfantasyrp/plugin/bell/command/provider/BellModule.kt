package com.fablesfantasyrp.plugin.bell.command.provider

import com.fablesfantasyrp.plugin.bell.data.entity.Bell
import com.fablesfantasyrp.plugin.bell.data.entity.EntityBellRepository
import com.sk89q.intake.parametric.AbstractModule

class BellModule(private val bells: EntityBellRepository) : AbstractModule() {
	override fun configure() {
		bind(Bell::class.java).toProvider(BellProvider(bells))
	}
}
