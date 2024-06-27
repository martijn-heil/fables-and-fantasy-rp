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
package com.fablesfantasyrp.plugin.time.javatime.temporal

import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalQuery

object FablesTemporalQueries {
	private val LOCAL_DATE: TemporalQuery<FablesLocalDate?> = object : TemporalQuery<FablesLocalDate?> {
		override fun queryFrom(temporal: TemporalAccessor): FablesLocalDate? {
			return if (temporal.isSupported(ChronoField.EPOCH_DAY)) FablesLocalDate.ofEpochDay(temporal.getLong(ChronoField.EPOCH_DAY)) else null
		}

		override fun toString(): String {
			return "LocalDate"
		}
	}

	fun localDate(): TemporalQuery<FablesLocalDate?> {
		return LOCAL_DATE
	}
}
