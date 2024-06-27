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
package com.fablesfantasyrp.plugin.knockout.data.persistent.database

import com.fablesfantasyrp.plugin.knockout.data.KnockoutState
import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerData
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageEvent
import java.time.Instant
import java.util.*

data class DatabaseKnockoutPlayerData(override val id: UUID,
									  override val state: KnockoutState? = null,
									  override var isKnockedOut: Boolean = false,
									  override var knockedOutAt: Instant? = null,
									  override var knockoutCause: EntityDamageEvent.DamageCause? = null,
									  override var knockoutDamager: Entity? = null) : PersistentKnockoutPlayerData {

}
