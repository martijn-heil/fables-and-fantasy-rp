package com.fablesfantasyrp.plugin.viewdistance

import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.CommandException
import com.fablesfantasyrp.caturix.Require
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["setviewdistance"], desc = "Set a player's view distance")
	@Require("fables.viewdistance.command.setviewdistance")
	fun setviewdistance(distance: Int, @CommandTarget("fables.viewdistance.command.setviewdistance.others") target: Player) {
		if (distance < 1 || distance > 32) throw CommandException("Please enter a distance between 1 and 32 chunks.")
		target.viewDistance = distance // This is a Paper Bukkit API extension
	}
}
