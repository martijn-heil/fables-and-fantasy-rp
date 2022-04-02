package com.fablesfantasyrp.plugin.chatchannelindicator

import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["chatchannelindicatorwidth"], desc = "Set chat channel indicator width")
	@Require("fables.chatchannelindicator.command.chatchannelindicator")
	fun halt(@Sender origin: Player, v: Int?) {
		var value = v
		check(value != null)
		if (value < 0) value = 0
		origin.chatChannelIndicatorWidth = value.toUInt()
	}
}
