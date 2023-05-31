package com.fablesfantasyrp.plugin.time

import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
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
