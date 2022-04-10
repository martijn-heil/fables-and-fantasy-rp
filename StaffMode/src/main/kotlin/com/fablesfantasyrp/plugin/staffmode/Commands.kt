package com.fablesfantasyrp.plugin.staffmode

import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
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
		FablesPlayer.forPlayer(target).isOnDuty = value
	}

	@Command(aliases = ["stafflist"], desc = "List staff members")
	@Require("fables.staffmode.command.stafflist")
	fun stafflist(@Sender sender: CommandSender) {
		val fablesPlayers = Bukkit.getOnlinePlayers().asSequence()
				.filter { it.hasPermission("fables.staffmode.command.duty") }
				.map { FablesPlayer.forPlayer(it) }

		val onDuty = fablesPlayers
				.filter { it.isOnDuty }
				.map { it.player }
				.map { PlaceholderAPI.setPlaceholders(it, "%vault_prefix_color%${it.name}" + RESET) }

		val offDuty = fablesPlayers
				.filter { !it.isOnDuty }
				.map { it.player }
				.map { PlaceholderAPI.setPlaceholders(it, "%vault_prefix_color%${it.name}" + RESET) }

		val sep = "${GRAY}, "
		sender.sendMessage("${GREEN}On ${GRAY}duty: " + onDuty.joinToString(sep))
		sender.sendMessage("${RED}Off ${GRAY}duty: " + offDuty.joinToString(sep))
	}
}
