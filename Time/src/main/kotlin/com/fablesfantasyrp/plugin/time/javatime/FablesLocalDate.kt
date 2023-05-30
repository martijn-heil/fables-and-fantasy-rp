package com.fablesfantasyrp.plugin.time.javatime

import com.fablesfantasyrp.plugin.time.FablesInstantSource
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.DAYS_0000_TO_1970
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.DAYS_IN_MONTH
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.DAYS_IN_WEEK
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.DAYS_IN_YEAR
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.MONTHS_IN_YEAR
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology.SECONDS_IN_DAY
import com.fablesfantasyrp.plugin.time.javatime.temporal.FablesTemporalQueries.localDate
import com.fablesfantasyrp.plugin.utils.Services
import java.time.*
import java.time.chrono.ChronoLocalDate
import java.time.chrono.IsoChronology
import java.time.chrono.IsoEra
import java.time.format.DateTimeFormatter
import java.time.temporal.*
import java.util.*
import java.util.stream.LongStream
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.math.max


class FablesLocalDate private constructor(val year: Int, private val month: Int, private val day: Int) : Temporal, TemporalAdjuster, ChronoLocalDate {

	override fun range(field: TemporalField): ValueRange {
		return if (field !is ChronoField) {
			field.rangeRefinedBy(this)
		} else if (field.isDateBased()) {
			when (field.ordinal) {
				1 -> ValueRange.of(1L, lengthOfMonth().toLong())
				2 -> ValueRange.of(1L, lengthOfYear().toLong())
				3 -> ValueRange.of(1L, 5L)
				4 -> if (year <= 0) ValueRange.of(1L, 1000000000L) else ValueRange.of(1L, 999999999L)
				else -> field.range()
			}
		} else {
			throw UnsupportedTemporalTypeException("Unsupported field: $field")
		}
	}

	override fun get(field: TemporalField): Int {
		return if (field is ChronoField) get0(field) else super<ChronoLocalDate>.get(field)
	}

	override fun getLong(field: TemporalField): Long {
		return if (field is ChronoField) {
			if (field === ChronoField.EPOCH_DAY) {
				toEpochDay()
			} else {
				if (field === ChronoField.PROLEPTIC_MONTH) prolepticMonth else get0(field).toLong()
			}
		} else {
			field.getFrom(this)
		}
	}

	private fun get0(field: TemporalField): Int {
		return when ((field as ChronoField)) {
			ChronoField.DAY_OF_MONTH -> day
			ChronoField.DAY_OF_YEAR -> dayOfYear
			ChronoField.ALIGNED_WEEK_OF_MONTH -> (day - 1) / DAYS_IN_WEEK + 1
			ChronoField.YEAR_OF_ERA -> if (year >= 1) year else 1 - year
			ChronoField.DAY_OF_WEEK -> dayOfWeek.value
			ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH -> (day - 1) % DAYS_IN_WEEK + 1
			ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR -> (dayOfYear - 1) % DAYS_IN_WEEK + 1
			ChronoField.EPOCH_DAY -> throw UnsupportedTemporalTypeException("Invalid field 'EpochDay' for get() method, use getLong() instead")
			ChronoField.ALIGNED_WEEK_OF_YEAR -> (dayOfYear - 1) / DAYS_IN_WEEK + 1
			ChronoField.MONTH_OF_YEAR -> month
			ChronoField.PROLEPTIC_MONTH -> throw UnsupportedTemporalTypeException("Invalid field 'ProlepticMonth' for get() method, use getLong() instead")
			ChronoField.YEAR -> year
			ChronoField.ERA -> if (year >= 1) 1 else 0
			else -> throw UnsupportedTemporalTypeException("Unsupported field: $field")
		}
	}

	private val prolepticMonth: Long
		get() = year.toLong() * MONTHS_IN_YEAR + month.toLong() - 1L

	override fun getChronology(): IsoChronology {
		return IsoChronology.INSTANCE
	}

	override fun getEra(): IsoEra {
		return if (year >= 1) IsoEra.CE else IsoEra.BCE
	}

	val monthValue: Int
		get() = month

	fun getMonth(): FablesMonth {
		return FablesMonth.of(month)
	}

	val dayOfMonth: Int
		get() = day
	val dayOfYear: Int
		get() = getMonth().firstDayOfYear(this.isLeapYear) + day - 1
	val dayOfWeek: FablesDayOfWeek
		get() {
			val dow0 = Math.floorMod(toEpochDay(), DAYS_IN_WEEK)
			return FablesDayOfWeek.of(dow0 + 1)
		}

	override fun isLeapYear(): Boolean {
		return false
	}

	override fun lengthOfMonth(): Int {
		return DAYS_IN_MONTH
	}

	override fun lengthOfYear(): Int {
		return DAYS_IN_MONTH * MONTHS_IN_YEAR
	}

	override fun with(adjuster: TemporalAdjuster): FablesLocalDate {
		return if (adjuster is FablesLocalDate) adjuster else (adjuster.adjustInto(this) as FablesLocalDate)
	}

	override fun with(field: TemporalField, newValue: Long): FablesLocalDate {
		return if (field is ChronoField) {
			field.checkValidValue(newValue)
			when (field.ordinal) {
				1 -> withDayOfMonth(newValue.toInt())
				2 -> withDayOfYear(newValue.toInt())
				3 -> plusWeeks(newValue - getLong(ChronoField.ALIGNED_WEEK_OF_MONTH))
				4 -> withYear((if (year >= 1) newValue else 1L - newValue).toInt())
				5 -> plusDays(newValue - dayOfWeek.value.toLong())
				6 -> plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH))
				7 -> plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR))
				8 -> ofEpochDay(newValue)
				9 -> plusWeeks(newValue - getLong(ChronoField.ALIGNED_WEEK_OF_YEAR))
				10 -> withMonth(newValue.toInt())
				11 -> plusMonths(newValue - prolepticMonth)
				12 -> withYear(newValue.toInt())
				13 -> if (getLong(ChronoField.ERA) == newValue) this else withYear(1 - year)
				else -> throw UnsupportedTemporalTypeException("Unsupported field: $field")
			}
		} else {
			field.adjustInto(this, newValue) as FablesLocalDate
		}
	}

	fun withYear(year: Int): FablesLocalDate {
		return if (this.year == year) {
			this
		} else {
			ChronoField.YEAR.checkValidValue(year.toLong())
			resolvePreviousValid(year, month, day)
		}
	}

	fun withMonth(month: Int): FablesLocalDate {
		return if (this.month == month) {
			this
		} else {
			ChronoField.MONTH_OF_YEAR.checkValidValue(month.toLong())
			resolvePreviousValid(year, month, day)
		}
	}

	fun withDayOfMonth(dayOfMonth: Int): FablesLocalDate {
		return if (day == dayOfMonth) this else of(year, month, dayOfMonth)
	}

	fun withDayOfYear(dayOfYear: Int): FablesLocalDate {
		return if (this.dayOfYear == dayOfYear) this else ofYearDay(year, dayOfYear)
	}

	override fun plus(amountToAdd: TemporalAmount): FablesLocalDate {
		return if (amountToAdd is Period) {
			plusMonths(amountToAdd.toTotalMonths()).plusDays(amountToAdd.days.toLong())
		} else {
			Objects.requireNonNull(amountToAdd, "amountToAdd")
			amountToAdd.addTo(this) as FablesLocalDate
		}
	}

	override fun plus(amountToAdd: Long, unit: TemporalUnit): FablesLocalDate {
		return if (unit is ChronoUnit) {
			when (unit.ordinal) {
				1 -> plusDays(amountToAdd)
				2 -> plusWeeks(amountToAdd)
				3 -> plusMonths(amountToAdd)
				4 -> plusYears(amountToAdd)
				5 -> plusYears(Math.multiplyExact(amountToAdd, 10))
				6 -> plusYears(Math.multiplyExact(amountToAdd, 100))
				7 -> plusYears(Math.multiplyExact(amountToAdd, 1000))
				8 -> this.with(ChronoField.ERA, Math.addExact(getLong(ChronoField.ERA), amountToAdd))
				else -> throw UnsupportedTemporalTypeException("Unsupported unit: $unit")
			}
		} else {
			unit.addTo(this, amountToAdd) as FablesLocalDate
		}
	}

	fun plusYears(yearsToAdd: Long): FablesLocalDate {
		return if (yearsToAdd == 0L) {
			this
		} else {
			val newYear = ChronoField.YEAR.checkValidIntValue(year.toLong() + yearsToAdd)
			resolvePreviousValid(newYear, month, day)
		}
	}

	fun plusMonths(monthsToAdd: Long): FablesLocalDate {
		return if (monthsToAdd == 0L) {
			this
		} else {
			val monthCount = year.toLong() * MONTHS_IN_YEAR + (month - 1).toLong()
			val calcMonths = monthCount + monthsToAdd
			val newYear = ChronoField.YEAR.checkValidIntValue(Math.floorDiv(calcMonths, MONTHS_IN_YEAR))
			val newMonth = Math.floorMod(calcMonths, MONTHS_IN_YEAR) + 1
			resolvePreviousValid(newYear, newMonth, day)
		}
	}

	fun plusWeeks(weeksToAdd: Long): FablesLocalDate {
		return plusDays(Math.multiplyExact(weeksToAdd, 8))
	}

	fun plusDays(daysToAdd: Long): FablesLocalDate {
		return if (daysToAdd == 0L) {
			this
		} else {
			val dom = day.toLong() + daysToAdd
			val monthLen: Long
			if (dom > 0L) {
				if (dom <= DAYS_IN_MONTH) {
					return FablesLocalDate(year, month, dom.toInt())
				}
				if (dom <= DAYS_IN_MONTH*2-1) {
					monthLen = lengthOfMonth().toLong()

					if (month < 12) {
						return FablesLocalDate(year, month + 1, (dom - monthLen).toInt())
					}
					ChronoField.YEAR.checkValidValue((year + 1).toLong())
					return FablesLocalDate(year + 1, 1, (dom - monthLen).toInt())
				}
			}
			monthLen = Math.addExact(toEpochDay(), daysToAdd)
			ofEpochDay(monthLen)
		}
	}

	override fun minus(amountToSubtract: TemporalAmount): FablesLocalDate {
		return if (amountToSubtract is Period) {
			minusMonths(amountToSubtract.toTotalMonths()).minusDays(amountToSubtract.days.toLong())
		} else {
			Objects.requireNonNull(amountToSubtract, "amountToSubtract")
			amountToSubtract.subtractFrom(this) as FablesLocalDate
		}
	}

	override fun minus(amountToSubtract: Long, unit: TemporalUnit): FablesLocalDate {
		return if (amountToSubtract == Long.MIN_VALUE) this.plus(Long.MAX_VALUE, unit).plus(1L, unit) else this.plus(-amountToSubtract, unit)
	}

	fun minusYears(yearsToSubtract: Long): FablesLocalDate {
		return if (yearsToSubtract == Long.MIN_VALUE) plusYears(Long.MAX_VALUE).plusYears(1L) else plusYears(-yearsToSubtract)
	}

	fun minusMonths(monthsToSubtract: Long): FablesLocalDate {
		return if (monthsToSubtract == Long.MIN_VALUE) plusMonths(Long.MAX_VALUE).plusMonths(1L) else plusMonths(-monthsToSubtract)
	}

	fun minusWeeks(weeksToSubtract: Long): FablesLocalDate {
		return if (weeksToSubtract == Long.MIN_VALUE) plusWeeks(Long.MAX_VALUE).plusWeeks(1L) else plusWeeks(-weeksToSubtract)
	}

	fun minusDays(daysToSubtract: Long): FablesLocalDate {
		return if (daysToSubtract == Long.MIN_VALUE) plusDays(Long.MAX_VALUE).plusDays(1L) else plusDays(-daysToSubtract)
	}

	override fun <R> query(query: TemporalQuery<R>): R {
		return if (query === TemporalQueries.localDate()) this as R else super<ChronoLocalDate>.query(query)
	}

	override fun adjustInto(temporal: Temporal): Temporal {
		return super.adjustInto(temporal)
	}

	override fun until(endExclusive: Temporal, unit: TemporalUnit): Long {
		val end = from(endExclusive)
		return if (unit is ChronoUnit) {
			when (unit.ordinal) {
				1 -> daysUntil(end)
				2 -> daysUntil(end) / DAYS_IN_WEEK
				3 -> monthsUntil(end)
				4 -> monthsUntil(end) / 12L
				5 -> monthsUntil(end) / 120L
				6 -> monthsUntil(end) / 1200L
				7 -> monthsUntil(end) / 12000L
				8 -> end.getLong(ChronoField.ERA) - getLong(ChronoField.ERA)
				else -> throw UnsupportedTemporalTypeException("Unsupported unit: $unit")
			}
		} else {
			unit.between(this, end)
		}
	}

	fun daysUntil(end: FablesLocalDate): Long {
		return end.toEpochDay() - toEpochDay()
	}

	private fun monthsUntil(end: FablesLocalDate): Long {
		val packed1 = prolepticMonth * DAYS_IN_MONTH + dayOfMonth.toLong()
		val packed2 = end.prolepticMonth * DAYS_IN_MONTH + end.dayOfMonth.toLong()
		return (packed2 - packed1) / DAYS_IN_MONTH
	}

	override fun until(endDateExclusive: ChronoLocalDate): Period {
		val end = from(endDateExclusive)
		var totalMonths = end.prolepticMonth - prolepticMonth
		var days = end.day - day
		if (totalMonths > 0L && days < 0) {
			--totalMonths
			val calcDate = plusMonths(totalMonths)
			days = (end.toEpochDay() - calcDate.toEpochDay()).toInt()
		} else if (totalMonths < 0L && days > 0) {
			++totalMonths
			days -= end.lengthOfMonth()
		}
		val years = totalMonths / MONTHS_IN_YEAR
		val months = (totalMonths % MONTHS_IN_YEAR).toInt()
		return Period.of(Math.toIntExact(years), months, days)
	}

	fun datesUntil(endExclusive: FablesLocalDate): Stream<FablesLocalDate> {
		val end = endExclusive.toEpochDay()
		val start = toEpochDay()
		return if (end < start) {
			throw IllegalArgumentException("$endExclusive < $this")
		} else {
			LongStream.range(start, end).mapToObj { epochDay: Long -> ofEpochDay(epochDay) }
		}
	}

	/*fun datesUntil(endExclusive: FablesLocalDate, step: Period): Stream<FablesLocalDate> { TODO convert
		return if (step.isZero) {
			throw IllegalArgumentException("step is zero")
		} else {
			val end = endExclusive.toEpochDay()
			val start = toEpochDay()
			val until = end - start
			val months = step.toTotalMonths()
			val days = step.days.toLong()
			if (months < 0L && days > 0L || months > 0L && days < 0L) {
				throw IllegalArgumentException("period months and days are of opposite sign")
			} else if (until == 0L) {
				Stream.empty()
			} else {
				val sign = if (months <= 0L && days <= 0L) -1 else 1
				if ((sign < 0) xor (until < 0L)) {
					throw IllegalArgumentException(endExclusive.toString() + (if (sign < 0) " > " else " < ") + this)
				} else {
					var steps: Long
					if (months == 0L) {
						steps = (until - sign.toLong()) / days
						LongStream.rangeClosed(0L, steps).mapToObj { n: Long -> ofEpochDay(start + n * days) }
					} else {
						steps = until * 1600L / (months * 48699L + days * 1600L) + 1L
						var addMonths = months * steps
						var addDays = days * steps
						val maxAddMonths = if (months > 0L) MAX.prolepticMonth - prolepticMonth else prolepticMonth - MIN.prolepticMonth
						if (addMonths * sign.toLong() > maxAddMonths || (plusMonths(addMonths).toEpochDay() + addDays) * sign.toLong() >= end * sign.toLong()) {
							--steps
							addMonths -= months
							addDays -= days
							if (addMonths * sign.toLong() > maxAddMonths || (plusMonths(addMonths).toEpochDay() + addDays) * sign.toLong() >= end * sign.toLong()) {
								--steps
							}
						}
						LongStream.rangeClosed(0L, steps).mapToObj { n: Long -> plusMonths(months * n).plusDays(days * n) }
					}
				}
			}
		}
	}*/

	override fun format(formatter: DateTimeFormatter): String {
		Objects.requireNonNull(formatter, "formatter")
		return formatter.format(this)
	}

	override fun atTime(time: LocalTime): FablesLocalDateTime {
		return FablesLocalDateTime.of(this, time)
	}

	fun atTime(hour: Int, minute: Int): FablesLocalDateTime {
		return this.atTime(LocalTime.of(hour, minute))
	}

	fun atTime(hour: Int, minute: Int, second: Int): FablesLocalDateTime {
		return this.atTime(LocalTime.of(hour, minute, second))
	}

	fun atTime(hour: Int, minute: Int, second: Int, nanoOfSecond: Int): FablesLocalDateTime {
		return this.atTime(LocalTime.of(hour, minute, second, nanoOfSecond))
	}

	/*public OffsetDateTime atTime(OffsetTime time) {
		return OffsetDateTime.of(FablesLocalDateTime.of(this, time.toLocalTime()), time.getOffset());
	}*/
	fun atStartOfDay(): FablesLocalDateTime {
		return FablesLocalDateTime.of(this, LocalTime.MIDNIGHT)
	}

	/*public ZonedDateTime atStartOfDay(ZoneId zone) {
		Objects.requireNonNull(zone, "zone");
		FablesLocalDateTime ldt = this.atTime(LocalTime.MIDNIGHT);
		if (!(zone instanceof ZoneOffset)) {
			ZoneRules rules = zone.getRules();
			ZoneOffsetTransition trans = rules.getTransition(ldt);
			if (trans != null && trans.isGap()) {
				ldt = trans.getDateTimeAfter();
			}
		}

		return ZonedDateTime.of(ldt, zone);
	}*/
	override fun toEpochDay(): Long {
		return year.toLong() * DAYS_IN_YEAR + (max(month - 1, 0)) * DAYS_IN_MONTH + day - DAYS_0000_TO_1970 - 1
	}

	fun toEpochSecond(time: LocalTime, offset: ZoneOffset): Long {
		Objects.requireNonNull(time, "time")
		Objects.requireNonNull(offset, "offset")
		var secs = toEpochDay() * SECONDS_IN_DAY + time.toSecondOfDay().toLong()
		secs -= offset.totalSeconds.toLong()
		return secs
	}

	override fun compareTo(other: ChronoLocalDate): Int {
		return if (other is FablesLocalDate) compareTo0(other) else super.compareTo(other)
	}

	fun compareTo0(otherDate: FablesLocalDate): Int {
		var cmp = year - otherDate.year
		if (cmp == 0) {
			cmp = month - otherDate.month
			if (cmp == 0) {
				cmp = day - otherDate.day
			}
		}
		return cmp
	}

	override fun isAfter(other: ChronoLocalDate): Boolean {
		return if (other is FablesLocalDate) {
			compareTo0(other) > 0
		} else {
			super.isAfter(other)
		}
	}

	override fun isBefore(other: ChronoLocalDate): Boolean {
		return if (other is FablesLocalDate) {
			compareTo0(other) < 0
		} else {
			super.isBefore(other)
		}
	}

	override fun isEqual(other: ChronoLocalDate): Boolean {
		return if (other is FablesLocalDate) {
			compareTo0(other) == 0
		} else {
			super.isEqual(other)
		}
	}

	override fun equals(other: Any?): Boolean {
		return if (this === other) {
			true
		} else if (other is FablesLocalDate) {
			compareTo0(other) == 0
		} else {
			false
		}
	}

	override fun hashCode(): Int {
		val yearValue = year
		val monthValue = month
		val dayValue = day
		return yearValue and -2048 xor (yearValue shl 11) + (monthValue shl 6) + dayValue
	}

	override fun toString(): String {
		val yearValue = year
		val monthValue = month
		val dayValue = day
		val absYear = abs(yearValue)
		val buf = StringBuilder(10)
		if (absYear < 1000) {
			if (yearValue < 0) {
				buf.append(yearValue - 10000).deleteCharAt(1)
			} else {
				buf.append(yearValue + 10000).deleteCharAt(0)
			}
		} else {
			if (yearValue > 9999) {
				buf.append('+')
			}
			buf.append(yearValue)
		}
		return buf.append(if (monthValue < 10) "-0" else "-").append(monthValue).append(if (dayValue < 10) "-0" else "-").append(dayValue).toString()
	} /*private Object writeReplace() {
		return new Ser((byte)3, this);
	}

	private void readObject(ObjectInputStream s) throws InvalidObjectException {
		throw new InvalidObjectException("Deserialization via serialization delegate");
	}

	void writeExternal(DataOutput out) throws IOException {
		out.writeInt(this.year);
		out.writeByte(this.month);
		out.writeByte(this.day);
	}

	static FablesLocalDate readExternal(DataInput in) throws IOException {
		int year = in.readInt();
		int month = in.readByte();
		int dayOfMonth = in.readByte();
		return of(year, month, dayOfMonth);
	}*/

	companion object {
		@JvmField
		val MIN = of(-999999999, 1, 1)
		@JvmField
		val MAX = of(999999999, 12, 31)
		val EPOCH = of(1970, 1, 1)

		fun now(): FablesLocalDate {
			return ofInstant(Services.get<FablesInstantSource>().instant())
		}

		@JvmStatic
		fun of(year: Int, month: Month, dayOfMonth: Int): FablesLocalDate {
			ChronoField.YEAR.checkValidValue(year.toLong())
			Objects.requireNonNull(month, "month")
			FablesChronology.range(ChronoField.DAY_OF_MONTH).checkValidValue(dayOfMonth.toLong(), ChronoField.DAY_OF_MONTH)
			require(dayOfMonth <= DAYS_IN_MONTH)
			return create(year, month.value, dayOfMonth)
		}

		@JvmStatic
		fun of(year: Int, month: Int, dayOfMonth: Int): FablesLocalDate {
			ChronoField.YEAR.checkValidValue(year.toLong())
			ChronoField.MONTH_OF_YEAR.checkValidValue(month.toLong())
			FablesChronology.range(ChronoField.DAY_OF_MONTH).checkValidValue(dayOfMonth.toLong(), ChronoField.DAY_OF_MONTH)
			return create(year, month, dayOfMonth)
		}

		fun ofYearDay(year: Int, dayOfYear: Int): FablesLocalDate {
			ChronoField.YEAR.checkValidValue(year.toLong())
			FablesChronology.range(ChronoField.DAY_OF_YEAR).checkValidValue(dayOfYear.toLong(), ChronoField.DAY_OF_YEAR)
			val leap = IsoChronology.INSTANCE.isLeapYear(year.toLong())
			var moy = Month.of((dayOfYear - 1) / 31 + 1)
			val monthEnd = moy.firstDayOfYear(leap) + moy.length(leap) - 1
			if (dayOfYear > monthEnd) {
				moy = moy.plus(1L)
			}
			val dom = dayOfYear - moy.firstDayOfYear(leap) + 1
			return FablesLocalDate(year, moy.value, dom)
		}

		fun ofInstant(instant: Instant): FablesLocalDate {
			Objects.requireNonNull(instant, "instant")
			val localSecond = instant.epochSecond
			val localEpochDay = Math.floorDiv(localSecond, SECONDS_IN_DAY)
			return ofEpochDay(localEpochDay)
		}

		@JvmStatic
		fun ofEpochDay(epochDay: Long): FablesLocalDate {
			ChronoField.EPOCH_DAY.checkValidValue(epochDay)
			val zeroDay = epochDay + DAYS_0000_TO_1970

			val year = (zeroDay / DAYS_IN_YEAR)
			val month = (zeroDay % DAYS_IN_YEAR) / DAYS_IN_MONTH + 1
			val day = (zeroDay % DAYS_IN_MONTH) + 1

			return FablesLocalDate(year.toInt(), month.toInt(), day.toInt())
		}

		@JvmStatic
		fun from(temporal: TemporalAccessor): FablesLocalDate {
			Objects.requireNonNull(temporal, "temporal")
			val date = temporal.query(localDate())
			return date ?: throw DateTimeException("Unable to obtain MyLocalDate from TemporalAccessor: " + temporal + " of type " + temporal.javaClass.name)
		}

		@JvmOverloads
		fun parse(text: CharSequence?, formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE): FablesLocalDate {
			Objects.requireNonNull(formatter, "formatter")
			return formatter.parse(text) { temporal: TemporalAccessor -> from(temporal) } as FablesLocalDate
		}

		private fun create(year: Int, month: Int, dayOfMonth: Int): FablesLocalDate {
			if (dayOfMonth > DAYS_IN_MONTH || dayOfMonth <= 0) {
				throw DateTimeException("Invalid date '" + Month.of(month).name + " " + dayOfMonth + "'")
			}
			return FablesLocalDate(year, month, dayOfMonth)
		}

		private fun resolvePreviousValid(year: Int, month: Int, day: Int): FablesLocalDate {
			return FablesLocalDate(year, month, day)
		}
	}
}
