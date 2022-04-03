package com.fablesfantasyrp.plugin.halt

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.utils.ess
import com.fablesfantasyrp.plugin.utils.isVanished
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["halt", "h"], desc = "Formally halt nearby players")
	@Require("fables.halt.command.halt")
	fun halt(@Sender origin: Player, @AllowCharacterName target: Player) {
		val targets = listOf(target)
		targets.asSequence()
				.filter { origin.location.distance(it.location) < 15 }
				.filter { !it.isVanished }
				.filter { !it.ess.isGodModeEnabled }
				.forEach { it.halt(origin) }
	}
}
