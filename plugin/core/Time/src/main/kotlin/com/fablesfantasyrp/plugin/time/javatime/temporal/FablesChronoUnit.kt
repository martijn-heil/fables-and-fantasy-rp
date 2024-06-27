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

import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_CENTURY
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_DAY
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_DECADE
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_HOUR
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_MILLENNIUM
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_MINUTE
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_MONTH
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_WEEK
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_YEAR
import java.time.Duration
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit

enum class FablesChronoUnit(private val customName: String, private val duration: Duration) : TemporalUnit {
	NANOS("Nanos", Duration.ofNanos(1L)),
	MICROS("Micros", Duration.ofNanos(1000L)),
	MILLIS("Millis", Duration.ofNanos(1000000L)),
	SECONDS("Seconds", Duration.ofSeconds(1L)),
	MINUTES("Minutes", Duration.ofSeconds(SECONDS_IN_MINUTE.toLong())),
	HOURS("Hours", Duration.ofSeconds(SECONDS_IN_HOUR.toLong())),
	HALF_DAYS("HalfDays", Duration.ofSeconds((SECONDS_IN_DAY / 2).toLong())),
	DAYS("Days", Duration.ofSeconds(SECONDS_IN_DAY.toLong())),
	WEEKS("Weeks", Duration.ofSeconds(SECONDS_IN_WEEK.toLong())),
	MONTHS("Months", Duration.ofSeconds(SECONDS_IN_MONTH.toLong())),
	YEARS("Years", Duration.ofSeconds(SECONDS_IN_YEAR)),
	DECADES("Decades", Duration.ofSeconds(SECONDS_IN_DECADE)),
	CENTURIES("Centuries", Duration.ofSeconds(SECONDS_IN_CENTURY)),
	MILLENNIA("Millennia", Duration.ofSeconds(SECONDS_IN_MILLENNIUM)),
	ERAS("Eras", Duration.ofSeconds(31556952000000000L)),
	FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999L));

	override fun getDuration(): Duration {
		return duration
	}

	override fun isDurationEstimated(): Boolean {
		return this.compareTo(DAYS) >= 0
	}

	override fun isDateBased(): Boolean {
		return this.compareTo(DAYS) >= 0 && this != FOREVER
	}

	override fun isTimeBased(): Boolean {
		return this.compareTo(DAYS) < 0
	}

	override fun isSupportedBy(temporal: Temporal): Boolean {
		return temporal.isSupported(this)
	}

	override fun <R : Temporal?> addTo(temporal: R, amount: Long): R {
		return temporal!!.plus(amount, this) as R
	}

	override fun between(temporal1Inclusive: Temporal, temporal2Exclusive: Temporal): Long {
		return temporal1Inclusive.until(temporal2Exclusive, this)
	}

	override fun toString(): String {
		return customName
	}
}
