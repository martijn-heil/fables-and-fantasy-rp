package com.fablesfantasyrp.plugin.knockout.command

import com.fablesfantasyrp.plugin.knockout.Permission
import com.fablesfantasyrp.plugin.knockout.knockout
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["knockout", "ko"], desc = "Knock out a player")
	@Require(Permission.Command.Knockout)
	fun knockout(target: Player, @CommandTarget by: Player?) {
		target.knockout.knockout(by)
	}

	@Command(aliases = ["revive"], desc = "Knock out a player")
	@Require(Permission.Command.Revive)
	fun revive(target: Player, @CommandTarget by: Player?) {
		target.knockout.revive(by)
	}
}
