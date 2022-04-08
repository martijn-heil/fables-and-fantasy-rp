package com.fablesfantasyrp.plugin.halt

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.utils.ess
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.ChatColor.GRAY
import org.bukkit.ChatColor.RED
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["halt", "h"], desc = "Formally halt nearby players")
	@Require("fables.halt.command.halt")
	fun halt(@Sender origin: Player, @AllowCharacterName target: Player) {
		val targets = listOf(target).filter { !it.ess.isVanished }
		for (it in targets) {
			if (it.location.distance(origin.location) > 15) {
				origin.sendMessage("$SYSPREFIX ${RED}Failed to halt ${GRAY}${it.currentPlayerCharacter.name}${RED} " +
						"because this player is too far away!")
				continue
			}

			if (it.ess.isGodModeEnabled) {
				origin.sendMessage("$SYSPREFIX ${RED} Failed to halt ${GRAY}${it.currentPlayerCharacter.name}${RED} " +
						"because this player is in god mode!")
				continue
			}

			it.halt(origin)
		}
	}
}
