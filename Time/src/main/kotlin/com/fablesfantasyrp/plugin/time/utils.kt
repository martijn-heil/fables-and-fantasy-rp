package com.fablesfantasyrp.plugin.time

import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDateTime
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import java.time.format.TextStyle

fun dateCard(dateTime: FablesLocalDateTime): Component {
	val dayOfWeekName = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL)
	val monthName = dateTime.month.getDisplayName(TextStyle.FULL)

	return miniMessage.deserialize(
		"<gray>" +
			"<prefix> Date and Time information:<newline>" +
			"<newline>" +
			"Time: <white><time></white><newline>" +
			"Date: <white><date></white><newline>" +
			"(<date_fancy>)<newline>" +
			"</gray>",
		Placeholder.component("prefix", legacyText(SYSPREFIX)),
		Placeholder.unparsed("time", "${String.format("%02d", dateTime.minute)}:${String.format("%02d", dateTime.minute)}"),
		Placeholder.unparsed("date", "${dateTime.dayOfMonth}.${dateTime.monthValue}.${dateTime.year}"),
		Placeholder.unparsed("date_fancy", "$dayOfWeekName ${dateTime.dayOfMonth}, $monthName, ${dateTime.year}")
	)
}
