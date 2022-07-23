package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.database.entity.AbstractEntityMapper
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerData
import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerDataRepository
import java.util.*

class ChatPlayerDataEntityMapper(private val child: PersistentKnockoutPlayerDataRepository)
	: AbstractEntityMapper<UUID, PersistentKnockoutPlayerData, KnockoutPlayerEntity, PersistentKnockoutPlayerDataRepository>(child),
		HasDirtyMarker<KnockoutPlayerEntity> {
	override var dirtyMarker: DirtyMarker<KnockoutPlayerEntity>? = null

	override fun forId(id: UUID): KnockoutPlayerEntity? = child.forId(id)?.let { convert(it) }
	override fun all(): Collection<KnockoutPlayerEntity> = child.all().map { convert(it) }

	private fun convert(it: PersistentKnockoutPlayerData): KnockoutPlayerEntity {
		val obj = KnockoutPlayerDataEntity(it.id, it.knockedOutAt)
		obj.dirtyMarker = dirtyMarker
		return obj
	}
}
