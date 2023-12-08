package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerData
import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerDataRepository
import java.util.*

class KnockoutPlayerDataEntityMapper(private val child: PersistentKnockoutPlayerDataRepository)
	: MappingRepository<UUID, PersistentKnockoutPlayerData, KnockoutPlayerEntity, PersistentKnockoutPlayerDataRepository>(child),
	HasDirtyMarker<KnockoutPlayerEntity> {
	override var dirtyMarker: DirtyMarker<KnockoutPlayerEntity>? = null

	override fun forId(id: UUID): KnockoutPlayerEntity? = child.forId(id)?.let { convertFromChild(it) }
	override fun convertToChild(v: KnockoutPlayerEntity): PersistentKnockoutPlayerData = v

	override fun all(): Collection<KnockoutPlayerEntity> = child.all().map { convertFromChild(it) }

	override fun convertFromChild(v: PersistentKnockoutPlayerData): KnockoutPlayerEntity {
		val obj = KnockoutPlayerDataEntity(v.id,
				state = v.state,
				knockedOutAt = v.knockedOutAt,
				knockoutCause = v.knockoutCause,
				knockoutDamager = v.knockoutDamager)
		obj.dirtyMarker = dirtyMarker
		return obj
	}
}
