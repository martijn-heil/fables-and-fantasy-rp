package com.fablesfantasyrp.plugin.lodestones.command.provider

import com.fablesfantasyrp.plugin.database.command.SimpleNamedEntityProvider
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository

class LodestoneProvider(lodestones: LodestoneRepository) : SimpleNamedEntityProvider<Lodestone, LodestoneRepository>(lodestones) {
	override val entityName: String = "Lodestone"
}
