package com.fablesfantasyrp.plugin.bell.command.provider

import com.fablesfantasyrp.plugin.bell.data.entity.Bell
import com.fablesfantasyrp.plugin.bell.data.entity.EntityBellRepository
import com.fablesfantasyrp.plugin.database.command.SimpleNamedEntityProvider

class BellProvider(bells: EntityBellRepository) : SimpleNamedEntityProvider<Bell, EntityBellRepository>(bells) {
	override val entityName: String = "Bell"
}
