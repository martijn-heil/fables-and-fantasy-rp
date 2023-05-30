package com.fablesfantasyrp.plugin.time.javatime

import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology
import com.fablesfantasyrp.plugin.time.javatime.temporal.FablesChronoUnit
import java.time.DateTimeException
import java.time.chrono.Chronology
import java.time.chrono.IsoChronology
import java.time.format.TextStyle
import java.time.temporal.*
import java.util.*

enum class FablesMonth : TemporalAccessor, TemporalAdjuster {
	NEWDAWN,
	STARFALL,
	FIRSTSEED,
	HIGHBLOOM,
	GREENFALL,
	HIGHSUN,
	GOLDSCORCH,
	SUNFALL,
	EDENSREST,
	RAINSWRATH,
	REDFALL,
	GODSNIGHT;

	val value: Int
		get() = ordinal + 1

	fun getDisplayName(style: TextStyle?, locale: Locale? = null): String {
		return when (style ?: TextStyle.FULL) {
			TextStyle.FULL, TextStyle.FULL_STANDALONE -> name.lowercase().replaceFirstChar { it.uppercaseChar() }
			TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> name.lowercase().first().lowercase()
			TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> name.take(3).lowercase().replaceFirstChar { it.uppercaseChar() }
		}
	}

	override fun isSupported(field: TemporalField?): Boolean {
		return if (field is ChronoField) {
			field === ChronoField.MONTH_OF_YEAR
		} else {
			field != null && field.isSupportedBy(this)
		}
	}

	override fun range(field: TemporalField): ValueRange {
		return if (field === ChronoField.MONTH_OF_YEAR) field.range() else super<TemporalAccessor>.range(field)
	}

	override fun get(field: TemporalField): Int {
		return if (field === ChronoField.MONTH_OF_YEAR) value else super<TemporalAccessor>.get(field)
	}

	override fun getLong(field: TemporalField): Long {
		return if (field === ChronoField.MONTH_OF_YEAR) {
			value.toLong()
		} else if (field is ChronoField) {
			throw UnsupportedTemporalTypeException("Unsupported field: $field")
		} else {
			field.getFrom(this)
		}
	}

	operator fun plus(months: Long): FablesMonth {
		val amount = (months % 12L).toInt()
		return ENUMS[(ordinal + amount + 12) % 12]
	}

	operator fun minus(months: Long): FablesMonth {
		return this.plus(-(months % 12L))
	}

	fun length(): Int {
		return FablesChronology.DAYS_IN_MONTH
	}

	fun firstDayOfYear(leapYear: Boolean): Int {
		return this.ordinal * FablesChronology.DAYS_IN_MONTH + 1
	}

	fun firstFablesMonthOfQuarter(): FablesMonth {
		return ENUMS[ordinal / 3 * 3]
	}

	override fun <R> query(query: TemporalQuery<R>): R {
		return if (query === TemporalQueries.chronology()) {
			FablesChronology as R
		} else {
			if (query === TemporalQueries.precision()) FablesChronoUnit.MONTHS as R else super.query(query)
		}
	}

	override fun adjustInto(temporal: Temporal): Temporal {
		return if (Chronology.from(temporal) != IsoChronology.INSTANCE) {
			throw DateTimeException("Adjustment only supported on ISO date-time")
		} else {
			temporal.with(ChronoField.MONTH_OF_YEAR, value.toLong())
		}
	}

	companion object {
		private val ENUMS = values()
		fun of(month: Int): FablesMonth {
			return if (month >= 1 && month <= FablesChronology.MONTHS_IN_YEAR) {
				ENUMS[month - 1]
			} else {
				throw DateTimeException("Invalid value for FablesMonthOfYear: $month")
			}
		}

		fun from(temporal: TemporalAccessor): FablesMonth {
			var temporal = temporal
			return if (temporal is FablesMonth) {
				temporal
			} else {
				try {
					if (IsoChronology.INSTANCE != Chronology.from(temporal)) {
						temporal = FablesLocalDate.from(temporal)
					}
					of(temporal[ChronoField.MONTH_OF_YEAR])
				} catch (var2: DateTimeException) {
					throw DateTimeException("Unable to obtain FablesMonth from TemporalAccessor: " + temporal + " of type " + temporal.javaClass.name, var2)
				}
			}
		}
	}
}
