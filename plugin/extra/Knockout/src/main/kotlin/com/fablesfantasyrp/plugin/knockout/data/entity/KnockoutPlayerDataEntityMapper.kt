/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.database.sync.repository.base.MappingRepository
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
