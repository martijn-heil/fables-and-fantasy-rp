package com.fablesfantasyrp.plugin.time.command

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.plugin.domain.EDEN
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.time.*
import com.fablesfantasyrp.plugin.time.gui.DatePicker
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDateTime
import com.github.shynixn.mccoroutine.bukkit.launch
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val gameClock: GameClock) {
	@Command(aliases = ["date"], desc = "Check the date and time.")
	@Require(Permission.Command.Date)
	fun date(@Sender sender: CommandSender) {
		val now = gameClock.instant()
		val dateTime = FablesLocalDateTime.ofInstant(now)
		val weather = if (sender is Player) DateCardWeatherInfo.ofLocation(sender.location) else null
		sender.sendMessage(dateCard(dateTime, weather))
	}

	@Command(aliases = ["pickdate"], desc = "Pick the date and time.")
	@Require(Permission.Command.Date)
	fun pickdate(@Sender sender: Player) {
		PLUGIN.launch { DatePicker(PLUGIN, "Pick a date").execute(sender) }
	}

	inner class DateTime {
		@Command(aliases = ["set"], desc = "Set the date and time.")
		@Require(Permission.Command.DateTime.Set)
		fun set(@Sender sender: CommandSender, year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
			val epochMillis = FablesLocalDateTime.of(year, month, dayOfMonth, hour, minute).toEpochSecond() * 1000
			gameClock.milliseconds = epochMillis
			sender.sendMessage("$SYSPREFIX Set time to $dayOfMonth.$month.$year $hour:$minute")
		}

		@Command(aliases = ["debuginfo"], desc = "Show date and time debug info")
		@Require(Permission.Command.DateTime.DebugInfo)
		fun debugInfo(@Sender sender: CommandSender) {
			val now = gameClock.instant()
			val epochMillis = now.toEpochMilli()
			val epochSecond = now.epochSecond
			val dateTime = FablesLocalDateTime.ofInstant(now)

			val timeString =
				String.format("%02d", dateTime.hour) + ":" +
				String.format("%02d", dateTime.minute)

			val dateString =
				String.format("%02d", dateTime.dayOfMonth) + "." +
				String.format("%02d", dateTime.monthValue) + "." +
				String.format("%04d", dateTime.year)

			sender.sendMessage(miniMessage.deserialize(
				"<gray>" +
				"<prefix> Datetime debug information:<newline>" +
				"Epoch seconds: <epoch_seconds><newline>" +
				"Epoch millis: <epoch_millis><newline>" +
				"Human readable: <datetime><newline>" +
				"Time in Eden: ${EDEN?.time}" +
				"</gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("epoch_seconds", epochSecond.toString()),
				Placeholder.unparsed("epoch_millis", epochMillis.toString()),
				Placeholder.unparsed("datetime", "$dateString $timeString")
			))
		}
	}
}
