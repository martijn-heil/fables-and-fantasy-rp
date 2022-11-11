package com.fablesfantasyrp.plugin.playerinstance

import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.command.CommandSender

class Commands {
	class PlayerInstance {
		@Command(aliases = ["list"], desc = "List player instances")
		@Require(Permission.Command.FastTravel.List)
		fun list(@Sender sender: CommandSender) {

		}
	}
}
