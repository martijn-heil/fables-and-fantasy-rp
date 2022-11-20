package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import com.fablesfantasyrp.plugin.text.sendError
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands {
	class PlayerInstance(private val instances: PlayerInstanceRepository) {
		@Command(aliases = ["list"], desc = "List player instances")
		@Require(Permission.Command.FastTravel.List)
		fun list(@Sender sender: Player) {
			for (instance in instances.forOwner(sender)) {
				sender.sendMessage("${instance.id}")
			}
		}

		@Command(aliases = ["create"], desc = "Create a new player instance")
		@Require(Permission.Command.FastTravel.List)
		fun create(@Sender sender: Player) {
			instances.create(com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance(
					id = 0,
					owner = sender)
			)
		}

		@Command(aliases = ["switch"], desc = "Switch to a new player instance")
		@Require(Permission.Command.FastTravel.List)
		fun switch(@Sender sender: Player, id: Int) {
			val instance = instances.forId(id) ?: run {
				sender.sendError("Player instance not found.")
				return
			}

			sender.currentPlayerInstance = instance
		}
	}
}
