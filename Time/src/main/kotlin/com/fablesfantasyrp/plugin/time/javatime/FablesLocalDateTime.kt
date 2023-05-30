package com.fablesfantasyrp.plugin.time.javatime

import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate.Companion.of
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate.Companion.ofEpochDay
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_DAY
import java.io.Serializable
import java.time.*
import java.time.chrono.ChronoLocalDateTime
import java.time.chrono.ChronoZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.*
import java.util.*

class FablesLocalDateTime private constructor(private val date: FablesLocalDate, private val time: LocalTime) : Temporal, TemporalAdjuster, ChronoLocalDateTime<FablesLocalDate>, Serializable {
	private fun with(newDate: FablesLocalDate, newTime: LocalTime): FablesLocalDateTime {
		return if (date === newDate && time === newTime) this else FablesLocalDateTime(newDate, newTime)
	}

	override fun isSupported(field: TemporalField?): Boolean {
		return if (field is ChronoField) {
			field.isDateBased() || field.isTimeBased()
		} else {
			field != null && field.isSupportedBy(this)
		}
	}

	override fun isSupported(unit: TemporalUnit): Boolean {
		return super.isSupported(unit)
	}

	override fun range(field: TemporalField): ValueRange {
		return if (field is ChronoField) {
			if (field.isTimeBased()) time.range(field) else date.range(field)
		} else {
			field.rangeRefinedBy(this)
		}
	}

	override fun get(field: TemporalField): Int {
		return if (field is ChronoField) {
			if (field.isTimeBased()) time[field] else date[field]
		} else {
			super<ChronoLocalDateTime>.get(field)
		}
	}

	override fun getLong(field: TemporalField): Long {
		return if (field is ChronoField) {
			if (field.isTimeBased()) time.getLong(field) else date.getLong(field)
		} else {
			field.getFrom(this)
		}
	}

	override fun toLocalDate(): FablesLocalDate {
		return date
	}

	val year: Int
		get() = date.year
	val monthValue: Int
		get() = date.monthValue
	val month: FablesMonth
		get() = date.getMonth()
	val dayOfMonth: Int
		get() = date.dayOfMonth
	val dayOfYear: Int
		get() = date.dayOfYear
	val dayOfWeek: FablesDayOfWeek
		get() = date.dayOfWeek

	override fun toLocalTime(): LocalTime {
		return time
	}

	val hour: Int
		get() = time.hour
	val minute: Int
		get() = time.minute
	val second: Int
		get() = time.second
	val nano: Int
		get() = time.nano

	override fun with(adjuster: TemporalAdjuster): FablesLocalDateTime {
		return if (adjuster is FablesLocalDate) {
			this.with(adjuster, time)
		} else if (adjuster is LocalTime) {
			this.with(date, adjuster)
		} else {
			if (adjuster is FablesLocalDateTime) adjuster else (adjuster.adjustInto(this) as FablesLocalDateTime)
		}
	}

	override fun with(field: TemporalField, newValue: Long): FablesLocalDateTime {
		return if (field is ChronoField) {
			if (field.isTimeBased()) this.with(date, time.with(field, newValue)) else this.with(date.with(field, newValue), time)
		} else {
			field.adjustInto(this, newValue) as FablesLocalDateTime
		}
	}

	fun withYear(year: Int): FablesLocalDateTime {
		return this.with(date.withYear(year), time)
	}

	fun withMonth(month: Int): FablesLocalDateTime {
		return this.with(date.withMonth(month), time)
	}

	fun withDayOfMonth(dayOfMonth: Int): FablesLocalDateTime {
		return this.with(date.withDayOfMonth(dayOfMonth), time)
	}

	fun withDayOfYear(dayOfYear: Int): FablesLocalDateTime {
		return this.with(date.withDayOfYear(dayOfYear), time)
	}

	fun withHour(hour: Int): FablesLocalDateTime {
		val newTime = time.withHour(hour)
		return this.with(date, newTime)
	}

	fun withMinute(minute: Int): FablesLocalDateTime {
		val newTime = time.withMinute(minute)
		return this.with(date, newTime)
	}

	fun withSecond(second: Int): FablesLocalDateTime {
		val newTime = time.withSecond(second)
		return this.with(date, newTime)
	}

	fun withNano(nanoOfSecond: Int): FablesLocalDateTime {
		val newTime = time.withNano(nanoOfSecond)
		return this.with(date, newTime)
	}

	fun truncatedTo(unit: TemporalUnit?): FablesLocalDateTime {
		return this.with(date, time.truncatedTo(unit))
	}

	override fun plus(amountToAdd: TemporalAmount): FablesLocalDateTime {
		return if (amountToAdd is Period) {
			this.with(date.plus(amountToAdd), time)
		} else {
			Objects.requireNonNull(amountToAdd, "amountToAdd")
			amountToAdd.addTo(this) as FablesLocalDateTime
		}
	}

	override fun plus(amountToAdd: Long, unit: TemporalUnit): FablesLocalDateTime {
		return if (unit is ChronoUnit) {
			when (unit) {
				ChronoUnit.NANOS -> plusNanos(amountToAdd)
				ChronoUnit.MICROS -> plusDays(amountToAdd / 86400000000L).plusNanos(amountToAdd % 86400000000L * 1000L)
				ChronoUnit.MILLIS -> plusDays(amountToAdd / 86400000L).plusNanos(amountToAdd % 86400000L * 1000000L)
				ChronoUnit.SECONDS -> plusSeconds(amountToAdd)
				ChronoUnit.MINUTES -> plusMinutes(amountToAdd)
				ChronoUnit.HOURS -> plusHours(amountToAdd)
				ChronoUnit.HALF_DAYS -> plusDays(amountToAdd / 256L).plusHours(amountToAdd % 256L * 12L)
				else -> this.with(date.plus(amountToAdd, unit), time)
			}
		} else {
			unit.addTo(this, amountToAdd) as FablesLocalDateTime
		}
	}

	fun plusYears(years: Long): FablesLocalDateTime {
		val newDate = date.plusYears(years)
		return this.with(newDate, time)
	}

	fun plusMonths(months: Long): FablesLocalDateTime {
		val newDate = date.plusMonths(months)
		return this.with(newDate, time)
	}

	fun plusWeeks(weeks: Long): FablesLocalDateTime {
		val newDate = date.plusWeeks(weeks)
		return this.with(newDate, time)
	}

	fun plusDays(days: Long): FablesLocalDateTime {
		val newDate = date.plusDays(days)
		return this.with(newDate, time)
	}

	fun plusHours(hours: Long): FablesLocalDateTime {
		return plusWithOverflow(date, hours, 0L, 0L, 0L, 1)
	}

	fun plusMinutes(minutes: Long): FablesLocalDateTime {
		return plusWithOverflow(date, 0L, minutes, 0L, 0L, 1)
	}

	fun plusSeconds(seconds: Long): FablesLocalDateTime {
		return plusWithOverflow(date, 0L, 0L, seconds, 0L, 1)
	}

	fun plusNanos(nanos: Long): FablesLocalDateTime {
		return plusWithOverflow(date, 0L, 0L, 0L, nanos, 1)
	}

	override fun minus(amountToSubtract: TemporalAmount): FablesLocalDateTime {
		return if (amountToSubtract is Period) {
			this.with(date.minus(amountToSubtract), time)
		} else {
			Objects.requireNonNull(amountToSubtract, "amountToSubtract")
			amountToSubtract.subtractFrom(this) as FablesLocalDateTime
		}
	}

	override fun minus(amountToSubtract: Long, unit: TemporalUnit): FablesLocalDateTime {
		return if (amountToSubtract == Long.MIN_VALUE) this.plus(Long.MAX_VALUE, unit).plus(1L, unit) else this.plus(-amountToSubtract, unit)
	}

	fun minusYears(years: Long): FablesLocalDateTime {
		return if (years == Long.MIN_VALUE) plusYears(Long.MAX_VALUE).plusYears(1L) else plusYears(-years)
	}

	fun minusMonths(months: Long): FablesLocalDateTime {
		return if (months == Long.MIN_VALUE) plusMonths(Long.MAX_VALUE).plusMonths(1L) else plusMonths(-months)
	}

	fun minusWeeks(weeks: Long): FablesLocalDateTime {
		return if (weeks == Long.MIN_VALUE) plusWeeks(Long.MAX_VALUE).plusWeeks(1L) else plusWeeks(-weeks)
	}

	fun minusDays(days: Long): FablesLocalDateTime {
		return if (days == Long.MIN_VALUE) plusDays(Long.MAX_VALUE).plusDays(1L) else plusDays(-days)
	}

	fun minusHours(hours: Long): FablesLocalDateTime {
		return plusWithOverflow(date, hours, 0L, 0L, 0L, -1)
	}

	fun minusMinutes(minutes: Long): FablesLocalDateTime {
		return plusWithOverflow(date, 0L, minutes, 0L, 0L, -1)
	}

	fun minusSeconds(seconds: Long): FablesLocalDateTime {
		return plusWithOverflow(date, 0L, 0L, seconds, 0L, -1)
	}

	fun minusNanos(nanos: Long): FablesLocalDateTime {
		return plusWithOverflow(date, 0L, 0L, 0L, nanos, -1)
	}

	private fun plusWithOverflow(newDate: FablesLocalDate, hours: Long, minutes: Long, seconds: Long, nanos: Long, sign: Int): FablesLocalDateTime {
		return if (hours or minutes or seconds or nanos == 0L) {
			this.with(newDate, time)
		} else {
			var totDays = nanos / 86400000000000L + seconds / 86400L + minutes / 1440L + hours / 24L
			totDays *= sign.toLong()
			var totNanos = nanos % 86400000000000L + seconds % 86400L * 1000000000L + minutes % 1440L * 60000000000L + hours % 24L * 3600000000000L
			val curNoD = time.toNanoOfDay()
			totNanos = totNanos * sign.toLong() + curNoD
			totDays += Math.floorDiv(totNanos, 86400000000000L)
			val newNoD = Math.floorMod(totNanos, 86400000000000L)
			val newTime = if (newNoD == curNoD) time else LocalTime.ofNanoOfDay(newNoD)
			this.with(newDate.plusDays(totDays), newTime)
		}
	}

	override fun <R> query(query: TemporalQuery<R>): R {
		return if (query === TemporalQueries.localDate()) date as R else super<ChronoLocalDateTime>.query(query)
	}

	override fun adjustInto(temporal: Temporal): Temporal {
		return super.adjustInto(temporal)
	}


	override fun toEpochSecond(offset: ZoneOffset): Long {
		return toEpochSecond()
	}

	fun toEpochSecond(): Long {
		val epochDay = toLocalDate().toEpochDay()
		return epochDay * SECONDS_IN_DAY + toLocalTime().toSecondOfDay().toLong()
	}

	override fun until(endExclusive: Temporal, unit: TemporalUnit): Long {
		val end = from(endExclusive)
		return if (unit is ChronoUnit) {
			if (unit.isTimeBased()) {
				var amount = date.daysUntil(end.date)
				if (amount == 0L) {
					time.until(end.time, unit)
				} else {
					var timePart = end.time.toNanoOfDay() - time.toNanoOfDay()
					if (amount > 0L) {
						--amount
						timePart += 86400000000000L
					} else {
						++amount
						timePart -= 86400000000000L
					}
					when (unit) {
						ChronoUnit.NANOS -> amount = Math.multiplyExact(amount, 86400000000000L)
						ChronoUnit.MICROS -> {
							amount = Math.multiplyExact(amount, 86400000000L)
							timePart /= 1000L
						}

						ChronoUnit.MILLIS -> {
							amount = Math.multiplyExact(amount, 86400000L)
							timePart /= 1000000L
						}

						ChronoUnit.SECONDS -> {
							amount = Math.multiplyExact(amount, 86400)
							timePart /= 1000000000L
						}

						ChronoUnit.MINUTES -> {
							amount = Math.multiplyExact(amount, 1440)
							timePart /= 60000000000L
						}

						ChronoUnit.HOURS -> {
							amount = Math.multiplyExact(amount, 24)
							timePart /= 3600000000000L
						}

						ChronoUnit.HALF_DAYS -> {
							amount = Math.multiplyExact(amount, 2)
							timePart /= 43200000000000L
						}

						else -> {}
					}
					Math.addExact(amount, timePart)
				}
			} else {
				var endDate = end.date
				if (endDate.isAfter(date) && end.time.isBefore(time)) {
					endDate = endDate.minusDays(1L)
				} else if (endDate.isBefore(date) && end.time.isAfter(time)) {
					endDate = endDate.plusDays(1L)
				}
				date.until(endDate, unit)
			}
		} else {
			unit.between(this, end)
		}
	}

	override fun format(formatter: DateTimeFormatter): String {
		Objects.requireNonNull(formatter, "formatter")
		return formatter.format(this)
	}

	fun atOffset(offset: ZoneOffset?): OffsetDateTime {
		//return OffsetDateTime.of(this, offset);
		throw UnsupportedOperationException()
	}

	override fun atZone(zone: ZoneId): ChronoZonedDateTime<FablesLocalDate> {
		//return ZonedDateTime.of(this, zone);
		throw UnsupportedOperationException()
	}

	override fun compareTo(other: ChronoLocalDateTime<*>?): Int {
		return if (other is FablesLocalDateTime) compareTo0(other) else super.compareTo(other)
	}

	private fun compareTo0(other: FablesLocalDateTime): Int {
		var cmp = date.compareTo0(other.toLocalDate())
		if (cmp == 0) {
			cmp = time.compareTo(other.toLocalTime())
		}
		return cmp
	}

	override fun isAfter(other: ChronoLocalDateTime<*>?): Boolean {
		return if (other is FablesLocalDateTime) {
			compareTo0(other) > 0
		} else {
			super.isAfter(other)
		}
	}

	override fun isBefore(other: ChronoLocalDateTime<*>?): Boolean {
		return if (other is FablesLocalDateTime) {
			compareTo0(other) < 0
		} else {
			super.isBefore(other)
		}
	}

	override fun isEqual(other: ChronoLocalDateTime<*>?): Boolean {
		return if (other is FablesLocalDateTime) {
			compareTo0(other) == 0
		} else {
			super.isEqual(other)
		}
	}

	override fun equals(obj: Any?): Boolean {
		return if (this === obj) {
			true
		} else {
			val var10000: Boolean
			if (obj is FablesLocalDateTime) {
				if (date == obj.date && time == obj.time) {
					var10000 = true
					return var10000
				}
			}
			var10000 = false
			var10000
		}
	}

	override fun hashCode(): Int {
		return date.hashCode() xor time.hashCode()
	}

	override fun toString(): String {
		return date.toString() + 'T' + time.toString()
	}

	companion object {
		val MIN = of(FablesLocalDate.MIN, LocalTime.MIN)
		val MAX = of(FablesLocalDate.MAX, LocalTime.MAX)
		private const val serialVersionUID = 6207766400415563566L

		fun of(year: Int, month: Month?, dayOfMonth: Int, hour: Int, minute: Int): FablesLocalDateTime {
			val date = of(year, month!!, dayOfMonth)
			val time = LocalTime.of(hour, minute)
			return FablesLocalDateTime(date, time)
		}

		fun of(year: Int, month: Month?, dayOfMonth: Int, hour: Int, minute: Int, second: Int): FablesLocalDateTime {
			val date = of(year, month!!, dayOfMonth)
			val time = LocalTime.of(hour, minute, second)
			return FablesLocalDateTime(date, time)
		}

		fun of(year: Int, month: Month?, dayOfMonth: Int, hour: Int, minute: Int, second: Int, nanoOfSecond: Int): FablesLocalDateTime {
			val date = of(year, month!!, dayOfMonth)
			val time = LocalTime.of(hour, minute, second, nanoOfSecond)
			return FablesLocalDateTime(date, time)
		}

		fun of(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int): FablesLocalDateTime {
			val date = of(year, month, dayOfMonth)
			val time = LocalTime.of(hour, minute)
			return FablesLocalDateTime(date, time)
		}

		fun of(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int): FablesLocalDateTime {
			val date = of(year, month, dayOfMonth)
			val time = LocalTime.of(hour, minute, second)
			return FablesLocalDateTime(date, time)
		}

		fun of(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int, nanoOfSecond: Int): FablesLocalDateTime {
			val date = of(year, month, dayOfMonth)
			val time = LocalTime.of(hour, minute, second, nanoOfSecond)
			return FablesLocalDateTime(date, time)
		}

		fun of(date: FablesLocalDate, time: LocalTime): FablesLocalDateTime {
			Objects.requireNonNull(date, "date")
			Objects.requireNonNull(time, "time")
			return FablesLocalDateTime(date, time)
		}

		fun ofInstant(instant: Instant): FablesLocalDateTime {
			Objects.requireNonNull(instant, "instant")
			return ofEpochSecond(instant.epochSecond, instant.nano)
		}

		fun ofEpochSecond(epochSecond: Long, nanoOfSecond: Int): FablesLocalDateTime {
			ChronoField.NANO_OF_SECOND.checkValidValue(nanoOfSecond.toLong())
			val localEpochDay = Math.floorDiv(epochSecond, 86400)
			val secsOfDay = Math.floorMod(epochSecond, 86400)
			val date = ofEpochDay(localEpochDay)
			val time = LocalTime.ofNanoOfDay(secsOfDay.toLong() * 1000000000L + nanoOfSecond.toLong())
			return FablesLocalDateTime(date, time)
		}

		fun from(temporal: TemporalAccessor): FablesLocalDateTime {
			return if (temporal is FablesLocalDateTime) {
				temporal
			} /*else if (temporal instanceof ZonedDateTime) {
			return ((ZonedDateTime)temporal).toLocalDateTime();
		} else if (temporal instanceof OffsetDateTime) {
			return ((OffsetDateTime)temporal).toLocalDateTime();
		}*/ else {
				try {
					val date = FablesLocalDate.from(temporal)
					val time = LocalTime.from(temporal)
					FablesLocalDateTime(date, time)
				} catch (var3: DateTimeException) {
					throw DateTimeException("Unable to obtain FablesLocalDateTime from TemporalAccessor: " + temporal + " of type " + temporal.javaClass.name, var3)
				}
			}
		}

		@JvmOverloads
		fun parse(text: CharSequence?, formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME): FablesLocalDateTime {
			Objects.requireNonNull(formatter, "formatter")
			return formatter.parse(text) { temporal: TemporalAccessor -> from(temporal) } as FablesLocalDateTime
		}

		/*private Object writeReplace() {
		return new Ser((byte)5, this);
	}

	private void readObject(ObjectInputStream s) throws InvalidObjectException {
		throw new InvalidObjectException("Deserialization via serialization delegate");
	}

	void writeExternal(DataOutput out) throws IOException {
		this.date.writeExternal(out);
		this.time.writeExternal(out);
	}

	static FablesLocalDateTime readExternal(DataInput in) throws IOException {
		FablesLocalDate date = FablesLocalDate.readExternal(in);
		LocalTime time = LocalTime.readExternal(in);
		return of(date, time);
	}*/
	}
}
