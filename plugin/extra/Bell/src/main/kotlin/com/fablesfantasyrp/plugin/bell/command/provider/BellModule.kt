package com.fablesfantasyrp.plugin.bell.command.provider

import com.fablesfantasyrp.plugin.bell.domain.entity.Bell
import com.fablesfantasyrp.plugin.bell.domain.repository.BellRepository
import com.fablesfantasyrp.caturix.parametric.AbstractModule

class BellModule(private val bells: BellRepository) : AbstractModule() {
	override fun configure() {
		bind(Bell::class.java).toProvider(BellProvider(bells))
	}
}
