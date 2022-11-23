package com.fablesfantasyrp.plugin.playerinstance.command

import com.fablesfantasyrp.plugin.playerinstance.Permission
import com.fablesfantasyrp.plugin.playerinstance.SYSPREFIX
import com.fablesfantasyrp.plugin.playerinstance.currentPlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands {
	class CommandPlayerInstance(private val instances: PlayerInstanceRepository) {
		@Command(aliases = ["list"], desc = "List player instances")
		@Require(Permission.Command.CommandPlayerInstance.List)
		fun list(@Sender sender: Player,
				 @CommandTarget(Permission.Command.CommandPlayerInstance.List + ".others") owner: OfflinePlayer) {
			sender.sendMessage("$SYSPREFIX ${owner.name} has the following player instances:")
			for (instance in instances.forOwner(sender)) {
				sender.sendMessage("${ChatColor.GRAY}#${instance.id}")
			}
		}

		@Command(aliases = ["new"], desc = "Create a new player instance")
		@Require(Permission.Command.CommandPlayerInstance.New)
		fun new(@Sender sender: CommandSender,
				@CommandTarget(Permission.Command.CommandPlayerInstance.New + ".others") owner: OfflinePlayer) {
			val result = instances.create(PlayerInstance(
					id = 0,
					owner = owner)
			)
			sender.sendMessage("$SYSPREFIX Created player instance #${result.id} owned by ${owner.name}")
		}

		@Command(aliases = ["become"], desc = "Become a player instance")
		@Require(Permission.Command.CommandPlayerInstance.Become)
		fun become(@Sender sender: Player, instance: PlayerInstance) {
			sender.currentPlayerInstance = instance
			sender.sendMessage("$SYSPREFIX You are now player instance #${instance.id}")
		}
	}
}
