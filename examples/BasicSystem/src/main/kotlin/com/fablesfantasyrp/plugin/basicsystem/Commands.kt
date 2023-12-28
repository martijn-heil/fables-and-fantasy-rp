package com.fablesfantasyrp.plugin.basicsystem

import com.fablesfantasyrp.plugin.basicsystem.data.entity.EntityBasicSystemPlayerRepository
import com.fablesfantasyrp.plugin.basicsystem.data.entity.EntitySlidingDoorRepository
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val players: EntityBasicSystemPlayerRepository) {
	@Command(aliases = ["tryepicjump"], desc = "Try epic jump")
	@Require(Permission.Command.Tryepicjump)
	fun tryepicjump(@Sender sender: Player) {
		val basicSystemPlayer = players.forPlayer(sender)

		if (!basicSystemPlayer.isEpic) {
			sender.sendError("You are not epic enough :(")
			return
		}

		basicSystemPlayer.doEpicJump()
	}

	class Doors(private val doors: EntitySlidingDoorRepository) {
		@Command(aliases = ["list"], desc = "List fast travel links")
		@Require(Permission.Command.Slidingdoor.Count)
		fun list(@Sender sender: CommandSender) {
			sender.sendMessage("$SYSPREFIX There are ${doors.all().size} doors!")
		}
	}
}
