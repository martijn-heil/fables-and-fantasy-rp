package com.fablesfantasyrp.plugin.viewdistance

import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.sk89q.intake.Command
import com.sk89q.intake.CommandException
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["setviewdistance"], desc = "Set a player's view distance")
	@Require("fables.viewdistance.command.setviewdistance")
	fun setviewdistance(distance: Int, @CommandTarget("fables.viewdistance.command.setviewdistance.others") target: Player) {
		if (distance < 1 || distance > 32) throw CommandException("Please enter a distance between 1 and 32 chunks.")
		target.viewDistance = distance // This is a Paper Bukkit API extension
	}
}
