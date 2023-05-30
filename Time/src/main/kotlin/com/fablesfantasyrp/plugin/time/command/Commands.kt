package com.fablesfantasyrp.plugin.time.command

import com.fablesfantasyrp.plugin.time.GameClock
import com.fablesfantasyrp.plugin.time.Permission
import com.fablesfantasyrp.plugin.time.SYSPREFIX
import com.fablesfantasyrp.plugin.time.dateCard
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDateTime
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.command.CommandSender

class Commands(private val gameClock: GameClock) {
	@Command(aliases = ["date"], desc = "Check the date and time.")
	@Require(Permission.Command.Date)
	fun date(@Sender sender: CommandSender) {
		val now = gameClock.instant()
		val dateTime = FablesLocalDateTime.ofInstant(now)
		sender.sendMessage(dateCard(dateTime))
	}

	inner class DateTime {
		@Command(aliases = ["set"], desc = "Check the date and time.")
		@Require(Permission.Command.DateTime.Set)
		fun set(@Sender sender: CommandSender, year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
			val epochMillis = FablesLocalDateTime.of(year, month, dayOfMonth, hour, minute).toEpochSecond() * 1000
			gameClock.milliseconds = epochMillis
			sender.sendMessage("$SYSPREFIX Set time to $dayOfMonth.$month.$year $hour:$minute")
		}
	}
}
