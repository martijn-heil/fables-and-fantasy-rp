package com.fablesfantasyrp.plugin.time.javatime

import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.DAYS_IN_WEEK
import java.time.DateTimeException
import java.time.format.TextStyle
import java.time.temporal.*
import java.util.*

/*enum class FablesDayOfWeek {
	MOONDAY,
	HARVESTDAY,
	WINEDAY,
	THUNDERDAY,
	FIREDAY,
	MARKETDAY,
	SUNDAY,
	SAINTSDAY;

	private val displayName = name.lowercase().replaceFirstChar { it.uppercaseChar() }
	override fun toString() = displayName
}*/


enum class FablesDayOfWeek : TemporalAccessor, TemporalAdjuster {
	MOONDAY,
	HARVESTDAY,
	WINEDAY,
	THUNDERDAY,
	FIREDAY,
	MARKETDAY,
	SUNDAY,
	SAINTSDAY;

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
			field === ChronoField.DAY_OF_WEEK
		} else {
			field != null && field.isSupportedBy(this)
		}
	}

	override fun range(field: TemporalField): ValueRange {
		return if (field === ChronoField.DAY_OF_WEEK) field.range() else super.range(field)
	}

	override fun get(field: TemporalField): Int {
		return if (field === ChronoField.DAY_OF_WEEK) value else super.get(field)
	}

	override fun getLong(field: TemporalField): Long {
		return if (field === ChronoField.DAY_OF_WEEK) {
			value.toLong()
		} else if (field is ChronoField) {
			throw UnsupportedTemporalTypeException("Unsupported field: $field")
		} else {
			field.getFrom(this)
		}
	}

	operator fun plus(days: Long): FablesDayOfWeek {
		val amount = (days % DAYS_IN_WEEK).toInt()
		return ENUMS[(ordinal + amount + DAYS_IN_WEEK) % DAYS_IN_WEEK]
	}

	operator fun minus(days: Long): FablesDayOfWeek {
		return this.plus(-(days % DAYS_IN_WEEK))
	}

	override fun <R> query(query: TemporalQuery<R>): R {
		return if (query === TemporalQueries.precision()) ChronoUnit.DAYS as R else super.query(query)
	}

	override fun adjustInto(temporal: Temporal): Temporal {
		return temporal.with(ChronoField.DAY_OF_WEEK, value.toLong())
	}

	companion object {
		private val ENUMS: Array<FablesDayOfWeek> = FablesDayOfWeek.values()
		fun of(dayOfWeek: Int): FablesDayOfWeek {
			return if (dayOfWeek in 1..DAYS_IN_WEEK) {
				ENUMS[dayOfWeek - 1]
			} else {
				throw DateTimeException("Invalid value for DayOfWeek: $dayOfWeek")
			}
		}

		fun from(temporal: TemporalAccessor): FablesDayOfWeek {
			return if (temporal is FablesDayOfWeek) {
				temporal
			} else {
				try {
					of(temporal[ChronoField.DAY_OF_WEEK])
				} catch (var2: DateTimeException) {
					throw DateTimeException("Unable to obtain FablesDayOfWeek from TemporalAccessor: " + temporal + " of type " + temporal.javaClass.name, var2)
				}
			}
		}
	}
}
