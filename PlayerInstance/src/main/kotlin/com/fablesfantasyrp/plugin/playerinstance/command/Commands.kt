package com.fablesfantasyrp.plugin.playerinstance.command

import com.fablesfantasyrp.plugin.playerinstance.Permission
import com.fablesfantasyrp.plugin.playerinstance.currentPlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands {
	class CommandPlayerInstance(private val instances: PlayerInstanceRepository) {
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
			instances.create(PlayerInstance(
					id = 0,
					owner = sender)
			)
		}

		@Command(aliases = ["use"], desc = "Use a player instance")
		@Require(Permission.Command.FastTravel.List)
		fun switch(@Sender sender: Player, instance: PlayerInstance) {
			sender.currentPlayerInstance = instance
		}
	}
}
