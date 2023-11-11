package com.fablesfantasyrp.plugin.bell.command.provider

import com.fablesfantasyrp.plugin.bell.domain.entity.Bell
import com.fablesfantasyrp.plugin.bell.domain.repository.BellRepository
import com.fablesfantasyrp.plugin.database.command.SimpleNamedEntityProvider

class BellProvider(bells: BellRepository) : SimpleNamedEntityProvider<Bell, BellRepository>(bells) {
	override val entityName: String = "Bell"
}
