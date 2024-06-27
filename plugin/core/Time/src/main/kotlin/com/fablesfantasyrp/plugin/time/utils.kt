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
package com.fablesfantasyrp.plugin.time

import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDateTime
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import java.time.format.TextStyle

data class DateCardWeatherInfo(val isClearWeather: Boolean,
							   val isThundering: Boolean,
							   val temperature: Double) {
	companion object {
		fun ofLocation(location: Location) = DateCardWeatherInfo(
			location.world.isClearWeather,
			location.world.isThundering,
			location.block.temperature
		)
	}
}

fun formatDateLong(date: FablesLocalDate): String {
	val dayOfWeekName = date.dayOfWeek.getDisplayName(TextStyle.FULL)
	val monthName = date.getMonth().getDisplayName(TextStyle.FULL)
	return "$dayOfWeekName ${date.dayOfMonth}, $monthName, ${date.year}"
}

fun formatDateShort(date: FablesLocalDate): String {
	return String.format("%02d", date.dayOfMonth) + "." +
			String.format("%02d", date.monthValue) + "." +
			String.format("%04d", date.year)
}

fun dateCard(dateTime: FablesLocalDateTime, weather: DateCardWeatherInfo? = null): Component {
	val dayOfWeekName = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL)
	val monthName = dateTime.month.getDisplayName(TextStyle.FULL)

	val timeString =
		String.format("%02d", dateTime.hour) + ":" +
		String.format("%02d", dateTime.minute)

	val dateString =
		String.format("%02d", dateTime.dayOfMonth) + "." +
		String.format("%02d", dateTime.monthValue) + "." +
		String.format("%04d", dateTime.year)

	val weatherStatus = when {
		weather == null -> Component.empty()
		dateTime.hour >= 18 || dateTime.hour < 6 && weather.isClearWeather -> Component.text("☽ Clear").color(NamedTextColor.WHITE)
		weather.temperature > 0.2 && weather.isThundering -> Component.text("⛈ Thunderstorm").color(NamedTextColor.DARK_GRAY)
		weather.temperature > 0.2 && !weather.isClearWeather -> Component.text("\uD83C\uDF27 Raining").color(NamedTextColor.GRAY)
		weather.temperature <= 0.2 && !weather.isClearWeather -> Component.text("❄ Snowing").color(NamedTextColor.AQUA)
		else -> Component.text("☀ Sunny").color(NamedTextColor.YELLOW)
	}

	val season = Season.ofYear(dateTime.year)
	val seasonColor = when (season) {
		Season.LILITHS_VEIL -> NamedTextColor.AQUA
		Season.THE_EMERALD_DUSK -> NamedTextColor.GREEN
		Season.EDENS_SHINE -> NamedTextColor.YELLOW
		Season.THE_AMBER_DAWN -> NamedTextColor.RED
	}

	return miniMessage.deserialize(
		"<gray>" +
			"<prefix> Date and Time information:<newline>" +
			"<newline>" +
			"Time: <white><time></white><newline>" +
			"Date: <white><date></white><newline>" +
			"(<date_fancy>)<newline>" +
			"<newline>" +
			"<season> <weather>" +
			"</gray>",
		Placeholder.component("prefix", legacyText(SYSPREFIX)),
		Placeholder.unparsed("time", timeString),
		Placeholder.unparsed("date", dateString),
		Placeholder.unparsed("date_fancy", "$dayOfWeekName ${dateTime.dayOfMonth}, $monthName, ${dateTime.year}"),
		Placeholder.component("season", Component.text(season.toString()).color(seasonColor)),
		Placeholder.component("weather", weatherStatus)
	)
}
