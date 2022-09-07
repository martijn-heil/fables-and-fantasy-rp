package com.fablesfantasyrp.plugin.staffmode

import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.gitlab.martijn_heil.nincommands.common.Toggle
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
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

		val onDuty = players
				.filter { it.isOnDuty }
				.map { PlaceholderAPI.setPlaceholders(it, "%vault_prefix_color%${it.name}" + RESET) }
				.map { translateAlternateColorCodes('&', it) }

		val offDuty = players
				.filter { !it.isOnDuty }
				.map { PlaceholderAPI.setPlaceholders(it, "%vault_prefix_color%${it.name}" + RESET) }
				.map { translateAlternateColorCodes('&', it) }

		val sep = "${GRAY}, "
		sender.sendMessage("${GREEN}On ${GRAY}duty: " + onDuty.joinToString(sep))
		sender.sendMessage("${RED}Off ${GRAY}duty: " + offDuty.joinToString(sep))
	}

	@Command(aliases = ["updatecommands"], desc = "Execute org.bukkit.entity.Player#updateCommands() on a player")
	@Require("fables.staffmode.command.updatecommands")
	fun updatecommands(@CommandTarget("fables.staffmode.command.updatecommands.others") target: Player) {
		target.updateCommands()
	}
}
