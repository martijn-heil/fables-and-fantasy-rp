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
package com.fablesfantasyrp.plugin.time.javatime.chrono

import com.fablesfantasyrp.plugin.time.FablesInstantSource
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDateTime
import com.fablesfantasyrp.plugin.utils.Services
import java.time.*
import java.time.chrono.*
import java.time.format.ResolverStyle
import java.time.temporal.*
import java.util.*

object FablesChronology : Chronology {
	override fun getId(): String = "FablesCommon"
	override fun getCalendarType(): String? = null

	override fun date(era: Era, yearOfEra: Int, month: Int, dayOfMonth: Int): FablesLocalDate {
		return date(prolepticYear(era, yearOfEra), month, dayOfMonth)
	}

	override fun date(prolepticYear: Int, month: Int, dayOfMonth: Int): FablesLocalDate {
		return FablesLocalDate.of(prolepticYear, month, dayOfMonth)
	}

	override fun dateYearDay(era: Era, yearOfEra: Int, dayOfYear: Int): FablesLocalDate {
		return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear)
	}

	override fun dateYearDay(prolepticYear: Int, dayOfYear: Int): FablesLocalDate {
		return FablesLocalDate.ofYearDay(prolepticYear, dayOfYear)
	}

	override fun dateEpochDay(epochDay: Long): FablesLocalDate {
		return FablesLocalDate.ofEpochDay(epochDay)
	}

	override fun date(temporal: TemporalAccessor): FablesLocalDate {
		return FablesLocalDate.from(temporal)
	}

	override fun epochSecond(prolepticYear: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int, zoneOffset: ZoneOffset): Long {
		ChronoField.YEAR.checkValidValue(prolepticYear.toLong())
		ChronoField.MONTH_OF_YEAR.checkValidValue(month.toLong())
		if (dayOfMonth < 1 || dayOfMonth > DAYS_IN_MONTH) throw DateTimeException("")
		ChronoField.HOUR_OF_DAY.checkValidValue(hour.toLong())
		ChronoField.MINUTE_OF_HOUR.checkValidValue(minute.toLong())
		ChronoField.SECOND_OF_MINUTE.checkValidValue(second.toLong())

		val timeInSeconds = hour * SECONDS_IN_HOUR + minute * SECONDS_IN_MINUTE + second
		val totalDays = prolepticYear * DAYS_IN_YEAR + month * DAYS_IN_MONTH + dayOfMonth - DAYS_0000_TO_1970
		return totalDays.toLong() * SECONDS_IN_DAY + timeInSeconds
	}

	override fun localDateTime(temporal: TemporalAccessor): FablesLocalDateTime {
		return FablesLocalDateTime.from(temporal)
	}

	override fun zonedDateTime(temporal: TemporalAccessor): ZonedDateTime {
		return ZonedDateTime.from(temporal)
	}

	override fun zonedDateTime(instant: Instant, zone: ZoneId): ZonedDateTime {
		return ZonedDateTime.ofInstant(instant, zone)
	}

	override fun dateNow(): FablesLocalDate {
		return FablesLocalDate.ofInstant(Services.get<FablesInstantSource>().instant())
	}

	override fun dateNow(zone: ZoneId): FablesLocalDate {
		return dateNow()
	}

	override fun dateNow(clock: Clock): FablesLocalDate {
		Objects.requireNonNull(clock, "clock")
		return FablesLocalDate.ofInstant(clock.instant())
	}

	fun dateNow(instantSource: InstantSource): FablesLocalDate {
		return FablesLocalDate.ofInstant(instantSource.instant())
	}

	override fun isLeapYear(prolepticYear: Long): Boolean {
		return false
	}

	override fun prolepticYear(era: Era, yearOfEra: Int): Int {
		return if (era !is IsoEra) {
			throw ClassCastException("Era must be IsoEra")
		} else {
			if (era === IsoEra.CE) yearOfEra else 1 - yearOfEra
		}
	}

	override fun eraOf(eraValue: Int): IsoEra {
		return IsoEra.of(eraValue)
	}

	override fun eras(): List<Era> {
		return listOf<Era>(*IsoEra.values())
	}

	override fun range(field: ChronoField): ValueRange {
		return when(field) {
			ChronoField.DAY_OF_WEEK -> ValueRange.of(1, DAYS_IN_WEEK.toLong())
			ChronoField.DAY_OF_MONTH -> ValueRange.of(1, DAYS_IN_MONTH.toLong())
			ChronoField.DAY_OF_YEAR -> ValueRange.of(1, DAYS_IN_YEAR.toLong())
			ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR -> ValueRange.of(1, DAYS_IN_WEEK.toLong())
			ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH -> ValueRange.of(1, DAYS_IN_WEEK.toLong())
			else -> field.range()
		}
	}

	override fun period(years: Int, months: Int, days: Int): Period {
		return Period.of(years, months, days)
	}

	override fun resolveDate(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle): ChronoLocalDate? {
		return if (fieldValues.containsKey(ChronoField.EPOCH_DAY)) {
			dateEpochDay(fieldValues.remove(ChronoField.EPOCH_DAY)!!)
		} else {
			resolveProlepticMonth(fieldValues, resolverStyle)
			val resolved = resolveYearOfEra(fieldValues, resolverStyle)
			if (resolved != null) {
				resolved
			} else {
				if (fieldValues.containsKey(ChronoField.YEAR)) {
					if (fieldValues.containsKey(ChronoField.MONTH_OF_YEAR)) {
						if (fieldValues.containsKey(ChronoField.DAY_OF_MONTH)) {
							return this.resolveYMD(fieldValues, resolverStyle)
						}
						if (fieldValues.containsKey(ChronoField.ALIGNED_WEEK_OF_MONTH)) {
							if (fieldValues.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
								return resolveYMAA(fieldValues, resolverStyle)
							}
							if (fieldValues.containsKey(ChronoField.DAY_OF_WEEK)) {
								return resolveYMAD(fieldValues, resolverStyle)
							}
						}
					}
					if (fieldValues.containsKey(ChronoField.DAY_OF_YEAR)) {
						return this.resolveYD(fieldValues, resolverStyle)
					}
					if (fieldValues.containsKey(ChronoField.ALIGNED_WEEK_OF_YEAR)) {
						if (fieldValues.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
							return resolveYAA(fieldValues, resolverStyle)
						}
						if (fieldValues.containsKey(ChronoField.DAY_OF_WEEK)) {
							return resolveYAD(fieldValues, resolverStyle)
						}
					}
				}
				null
			}
		}
	}

	private fun resolveProlepticMonth(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle) {
		val pMonth = fieldValues.remove(ChronoField.PROLEPTIC_MONTH)
		if (pMonth != null) {
			if (resolverStyle != ResolverStyle.LENIENT) {
				ChronoField.PROLEPTIC_MONTH.checkValidValue(pMonth)
			}
			val chronoDate: ChronoLocalDate = this.dateNow().with(ChronoField.DAY_OF_MONTH, 1L).with(ChronoField.PROLEPTIC_MONTH, pMonth)
			addFieldValue(fieldValues, ChronoField.MONTH_OF_YEAR, chronoDate[ChronoField.MONTH_OF_YEAR].toLong())
			addFieldValue(fieldValues, ChronoField.YEAR, chronoDate[ChronoField.YEAR].toLong())
		}
	}

	private fun resolveYearOfEra(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle): ChronoLocalDate? {
		val yoeLong = fieldValues.remove(ChronoField.YEAR_OF_ERA)
		if (yoeLong != null) {
			val eraLong = fieldValues.remove(ChronoField.ERA)
			val yoe: Int = if (resolverStyle != ResolverStyle.LENIENT) {
				this.range(ChronoField.YEAR_OF_ERA).checkValidIntValue(yoeLong, ChronoField.YEAR_OF_ERA)
			} else {
				Math.toIntExact(yoeLong)
			}
			if (eraLong != null) {
				val eraObj: Era = eraOf(this.range(ChronoField.ERA).checkValidIntValue(eraLong, ChronoField.ERA))
				addFieldValue(fieldValues, ChronoField.YEAR, prolepticYear(eraObj, yoe).toLong())
			} else if (fieldValues.containsKey(ChronoField.YEAR)) {
				val year = this.range(ChronoField.YEAR).checkValidIntValue(fieldValues[ChronoField.YEAR]!!, ChronoField.YEAR)
				val chronoDate: ChronoLocalDate = this.dateYearDay(year, 1)
				addFieldValue(fieldValues, ChronoField.YEAR, prolepticYear(chronoDate.era, yoe).toLong())
			} else if (resolverStyle == ResolverStyle.STRICT) {
				fieldValues[ChronoField.YEAR_OF_ERA] = yoeLong
			} else {
				val eras = eras()
				if (eras.isEmpty()) {
					addFieldValue(fieldValues, ChronoField.YEAR, yoe.toLong())
				} else {
					addFieldValue(fieldValues, ChronoField.YEAR, prolepticYear(eras[eras.size - 1], yoe).toLong())
				}
			}
		} else if (fieldValues.containsKey(ChronoField.ERA)) {
			this.range(ChronoField.ERA).checkValidValue(fieldValues[ChronoField.ERA]!!, ChronoField.ERA)
		}
		return null
	}

	private fun resolveYMD(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle): ChronoLocalDate? {
		val y = this.range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR)!!, ChronoField.YEAR)
		return if (resolverStyle == ResolverStyle.LENIENT) {
			val months = Math.subtractExact(fieldValues.remove(ChronoField.MONTH_OF_YEAR)!!, 1L)
			val days = Math.subtractExact(fieldValues.remove(ChronoField.DAY_OF_MONTH)!!, 1L)
			this.date(y, 1, 1).plus(months, ChronoUnit.MONTHS).plus(days, ChronoUnit.DAYS)
		} else {
			val moy = this.range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.MONTH_OF_YEAR)!!, ChronoField.MONTH_OF_YEAR)
			val domRange = this.range(ChronoField.DAY_OF_MONTH)
			val dom = domRange.checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_MONTH)!!, ChronoField.DAY_OF_MONTH)
			if (resolverStyle == ResolverStyle.SMART) {
				try {
					this.date(y, moy, dom)
				} catch (var8: DateTimeException) {
					this.date(y, moy, 1).with(TemporalAdjusters.lastDayOfMonth())
				}
			} else {
				this.date(y, moy, dom)
			}
		}
	}

	private fun resolveYD(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle): ChronoLocalDate? {
		val y = this.range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR)!!, ChronoField.YEAR)
		return if (resolverStyle == ResolverStyle.LENIENT) {
			val days = Math.subtractExact(fieldValues.remove(ChronoField.DAY_OF_YEAR)!!, 1L)
			this.dateYearDay(y, 1).plus(days, ChronoUnit.DAYS)
		} else {
			val doy = this.range(ChronoField.DAY_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_YEAR)!!, ChronoField.DAY_OF_YEAR)
			this.dateYearDay(y, doy)
		}
	}

	private fun resolveYMAA(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle): ChronoLocalDate? {
		val y = this.range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR)!!, ChronoField.YEAR)
		return if (resolverStyle == ResolverStyle.LENIENT) {
			val months = Math.subtractExact(fieldValues.remove(ChronoField.MONTH_OF_YEAR)!!, 1L)
			val weeks = Math.subtractExact(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_MONTH)!!, 1L)
			val days = Math.subtractExact(fieldValues.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)!!, 1L)
			this.date(y, 1, 1).plus(months, ChronoUnit.MONTHS).plus(weeks, ChronoUnit.WEEKS).plus(days, ChronoUnit.DAYS)
		} else {
			val moy = this.range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.MONTH_OF_YEAR)!!, ChronoField.MONTH_OF_YEAR)
			val aw = this.range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_MONTH)!!, ChronoField.ALIGNED_WEEK_OF_MONTH)
			val ad = this.range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)!!, ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)
			val date: ChronoLocalDate = this.date(y, moy, 1).plus(((aw - 1) * DAYS_IN_WEEK + (ad - 1)).toLong(), ChronoUnit.DAYS)
			if (resolverStyle == ResolverStyle.STRICT && date[ChronoField.MONTH_OF_YEAR] != moy) {
				throw DateTimeException("Strict mode rejected resolved date as it is in a different month")
			} else {
				date
			}
		}
	}

	private fun resolveYMAD(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle): ChronoLocalDate? {
		val y = this.range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR)!!, ChronoField.YEAR)
		return if (resolverStyle == ResolverStyle.LENIENT) {
			val months = Math.subtractExact(fieldValues.remove(ChronoField.MONTH_OF_YEAR)!!, 1L)
			val weeks = Math.subtractExact(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_MONTH)!!, 1L)
			val dow = Math.subtractExact(fieldValues.remove(ChronoField.DAY_OF_WEEK)!!, 1L)
			resolveAligned(this.date(y, 1, 1), months, weeks, dow)
		} else {
			val moy = this.range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.MONTH_OF_YEAR)!!, ChronoField.MONTH_OF_YEAR)
			val aw = this.range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_MONTH)!!, ChronoField.ALIGNED_WEEK_OF_MONTH)
			val dow = this.range(ChronoField.DAY_OF_WEEK).checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_WEEK)!!, ChronoField.DAY_OF_WEEK)
			val date: ChronoLocalDate = this.date(y, moy, 1).plus(((aw - 1) * DAYS_IN_WEEK).toLong(), ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dow)))
			if (resolverStyle == ResolverStyle.STRICT && date[ChronoField.MONTH_OF_YEAR] != moy) {
				throw DateTimeException("Strict mode rejected resolved date as it is in a different month")
			} else {
				date
			}
		}
	}

	private fun resolveYAA(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle): ChronoLocalDate? {
		val y = this.range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR)!!, ChronoField.YEAR)
		return if (resolverStyle == ResolverStyle.LENIENT) {
			val weeks = Math.subtractExact(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_YEAR)!!, 1L)
			val days = Math.subtractExact(fieldValues.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)!!, 1L)
			this.dateYearDay(y, 1).plus(weeks, ChronoUnit.WEEKS).plus(days, ChronoUnit.DAYS)
		} else {
			val aw = this.range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_YEAR)!!, ChronoField.ALIGNED_WEEK_OF_YEAR)
			val ad = this.range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)!!, ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)
			val date: ChronoLocalDate = this.dateYearDay(y, 1).plus(((aw - 1) * DAYS_IN_WEEK + (ad - 1)).toLong(), ChronoUnit.DAYS)
			if (resolverStyle == ResolverStyle.STRICT && date[ChronoField.YEAR] != y) {
				throw DateTimeException("Strict mode rejected resolved date as it is in a different year")
			} else {
				date
			}
		}
	}

	private fun resolveYAD(fieldValues: MutableMap<TemporalField?, Long?>, resolverStyle: ResolverStyle): ChronoLocalDate? {
		val y = this.range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR)!!, ChronoField.YEAR)
		return if (resolverStyle == ResolverStyle.LENIENT) {
			val weeks = Math.subtractExact(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_YEAR)!!, 1L)
			val dow = Math.subtractExact(fieldValues.remove(ChronoField.DAY_OF_WEEK)!!, 1L)
			resolveAligned(this.dateYearDay(y, 1), 0L, weeks, dow)
		} else {
			val aw = this.range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_YEAR)!!, ChronoField.ALIGNED_WEEK_OF_YEAR)
			val dow = this.range(ChronoField.DAY_OF_WEEK).checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_WEEK)!!, ChronoField.DAY_OF_WEEK)
			val date: ChronoLocalDate = this.dateYearDay(y, 1).plus(((aw - 1) * DAYS_IN_WEEK).toLong(), ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dow)))
			if (resolverStyle == ResolverStyle.STRICT && date[ChronoField.YEAR] != y) {
				throw DateTimeException("Strict mode rejected resolved date as it is in a different year")
			} else {
				date
			}
		}
	}

	private fun resolveAligned(base: ChronoLocalDate, months: Long, weeks: Long, dow: Long): ChronoLocalDate? {
		var dow = dow
		var date = base.plus(months, ChronoUnit.MONTHS).plus(weeks, ChronoUnit.WEEKS)
		if (dow > DAYS_IN_WEEK) {
			date = date.plus((dow - 1L) / DAYS_IN_WEEK, ChronoUnit.WEEKS)
			dow = (dow - 1L) % DAYS_IN_WEEK + 1L
		} else if (dow < 1L) {
			date = date.plus(Math.subtractExact(dow, DAYS_IN_WEEK.toLong()) / DAYS_IN_WEEK, ChronoUnit.WEEKS)
			dow = (dow + DAYS_IN_WEEK-1) % DAYS_IN_WEEK + 1L
		}
		return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dow.toInt())))
	}

	private fun addFieldValue(fieldValues: MutableMap<TemporalField?, Long?>, field: ChronoField, value: Long) {
		val old = fieldValues[field]
		if (old != null && old != value) {
			throw DateTimeException("Conflict found: $field $old differs from $field $value")
		} else {
			fieldValues[field] = value
		}
	}

	override fun compareTo(other: Chronology): Int {
		return this.id.compareTo(other.id)
	}

	override fun equals(other: Any?): Boolean {
		return if (this === other) {
			true
		} else if (other is AbstractChronology) {
			this.compareTo(other as Chronology) == 0
		} else {
			false
		}
	}

	override fun hashCode(): Int {
		return this.javaClass.hashCode() xor this.id.hashCode()
	}

	override fun toString(): String {
		return this.id
	}

	const val MILLISECONDS_IN_SECOND = 1000
	const val SECONDS_IN_MINUTE = 60
	const val MINUTES_IN_HOUR = 60
	const val HOURS_IN_DAY = 24
	const val DAYS_IN_WEEK = 8
	const val WEEKS_IN_MONTH = 4
	const val MONTHS_IN_YEAR = 12
	const val YEARS_IN_DECADE = 10
	const val DECADES_IN_CENTURY = 10
	const val CENTURIES_IN_MILLENNIUM = 10

	const val DAYS_IN_MONTH = DAYS_IN_WEEK * 4
	const val DAYS_IN_YEAR = MONTHS_IN_YEAR * DAYS_IN_MONTH
	const val WEEKS_IN_YEAR = WEEKS_IN_MONTH * MONTHS_IN_YEAR
	const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR
	const val SECONDS_IN_DAY = SECONDS_IN_HOUR * HOURS_IN_DAY
	const val SECONDS_IN_WEEK = SECONDS_IN_DAY * DAYS_IN_WEEK
	const val SECONDS_IN_MONTH = SECONDS_IN_WEEK * WEEKS_IN_MONTH
	const val SECONDS_IN_YEAR: Long = SECONDS_IN_MONTH.toLong() * MONTHS_IN_YEAR
	const val SECONDS_IN_DECADE: Long = SECONDS_IN_YEAR * YEARS_IN_DECADE
	const val SECONDS_IN_CENTURY: Long = SECONDS_IN_DECADE * DECADES_IN_CENTURY
	const val SECONDS_IN_MILLENNIUM: Long = SECONDS_IN_CENTURY * CENTURIES_IN_MILLENNIUM

	const val MILLISECONDS_IN_MINUTE = MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE
	const val MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE * MINUTES_IN_HOUR
	const val MILLISECONDS_IN_DAY = MILLISECONDS_IN_HOUR * HOURS_IN_DAY
	const val MILLISECONDS_IN_WEEK = MILLISECONDS_IN_DAY * DAYS_IN_WEEK
	const val MILLISECONDS_IN_MONTH: Long = MILLISECONDS_IN_WEEK.toLong() * WEEKS_IN_MONTH
	const val MILLISECONDS_IN_YEAR: Long = MILLISECONDS_IN_MONTH * MONTHS_IN_YEAR

	const val DAYS_0000_TO_1970 = DAYS_IN_YEAR * 1970
}
