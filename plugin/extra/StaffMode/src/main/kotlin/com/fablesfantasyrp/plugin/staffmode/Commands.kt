package com.fablesfantasyrp.plugin.staffmode

import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.playerNameStyle
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.gitlab.martijn_heil.nincommands.common.Toggle
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["duty"], desc = "Toggle your staff duty status on/off")
	@Require("fables.staffmode.command.duty")
	fun duty(@Toggle value: Boolean, @CommandTarget("fables.staffmode.command.duty.others") target: Player) {
		target.isOnDuty = value
	}

	@Command(aliases = ["stafflist"], desc = "List staff members")
	@Require("fables.staffmode.command.stafflist")
	fun stafflist(@Sender sender: CommandSender) {
		val players = Bukkit.getOnlinePlayers().asSequence()
				.filter { it.hasPermission("fables.staffmode.command.duty") }

		val onDuty = Component.text().append(players
				.filter { it.isOnDuty }
				.map { Component.text(it.name).style(it.playerNameStyle) }
				.join(Component.text(", ")).toList())

		val offDuty = Component.text().append(players
				.filter { !it.isOnDuty }
				.map { Component.text(it.name).style(it.playerNameStyle) }
				.join(Component.text(", ")).toList())

		sender.sendMessage(miniMessage.deserialize(
				"<green>On</green> <gray>duty: <onduty></gray><newline>" +
						"<red>Off</red> <gray>duty: <offduty></gray>",
				Placeholder.component("onduty", onDuty),
				Placeholder.component("offduty", offDuty)))
	}

	@Command(aliases = ["updatecommands"], desc = "Execute org.bukkit.entity.Player#updateCommands() on a player")
	@Require("fables.staffmode.command.updatecommands")
	fun updatecommands(@CommandTarget("fables.staffmode.command.updatecommands.others") target: Player) {
		target.updateCommands()
	}
}
