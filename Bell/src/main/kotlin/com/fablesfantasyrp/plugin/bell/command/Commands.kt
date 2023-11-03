package com.fablesfantasyrp.plugin.bell.command

import com.fablesfantasyrp.plugin.bell.Permission
import com.fablesfantasyrp.plugin.bell.SYSPREFIX
import com.fablesfantasyrp.plugin.bell.data.entity.Bell
import com.fablesfantasyrp.plugin.bell.data.entity.EntityBellRepository
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import dev.kord.common.entity.Snowflake
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val bells: EntityBellRepository) {
	inner class BellCommand {
		@Command(aliases = ["list"], desc = "List bells")
		@Require(Permission.Command.Bell.List)
		fun list(@Sender sender: CommandSender) {
			sender.sendMessage(miniMessage.deserialize("<gray><prefix> Bells:<newline><bells></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.component("bells",
					Component.join(JoinConfiguration.newlines(),
						bells.all().map { Component.text("#${it.id} ${it.locationName} ${it.location.toLocation().humanReadable()}" ) }
					)
				)
			))
		}

		@Command(aliases = ["destroy"], desc = "Destroy bell")
		@Require(Permission.Command.Bell.Destroy)
		fun destroy(@Sender sender: CommandSender, bell: Bell) {
			bells.destroy(bell)
			sender.sendMessage("$SYSPREFIX Destroyed bell #${bell.id} ${bell.locationName}")
		}

		@Command(aliases = ["create"], desc = "Create a bell")
		@Require(Permission.Command.Bell.Create)
		fun create(@Sender sender: Player, locationName: String, discordChannelId: String, roleIds: String) {
			val block = sender.getTargetBlock(10)
			if (block == null || block.type != Material.BELL) {
				sender.sendError("Please aim at a bell block.")
				return
			}

			val discordChannelFlake = Snowflake(discordChannelId)
			val discordRoleFlakes = roleIds.split(',').map { Snowflake(it) }.toSet()

			if (bells.nameExists(locationName)) {
				sender.sendError("The name '$locationName' is already taken. Please use another name.")
				return
			}

			val bell = bells.create(Bell(
				id = 0,
				location = block.location.toBlockIdentifier(),
				locationName,
				discordChannelId = discordChannelFlake,
				discordRoleIds = discordRoleFlakes
			))

			sender.sendMessage("$SYSPREFIX Created bell #${bell.id}")
		}
	}
}
