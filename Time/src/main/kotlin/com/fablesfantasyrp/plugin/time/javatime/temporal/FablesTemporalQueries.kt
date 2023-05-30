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
