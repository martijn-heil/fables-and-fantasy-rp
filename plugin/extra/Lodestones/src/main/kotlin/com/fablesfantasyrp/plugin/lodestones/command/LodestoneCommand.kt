package com.fablesfantasyrp.plugin.lodestones.command

import com.fablesfantasyrp.plugin.lodestones.Permission
import com.fablesfantasyrp.plugin.lodestones.SYSPREFIX
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LodestoneCommand(private val lodestones: LodestoneRepository) {
	@Command(aliases = ["list"], desc = "List lodestones")
	@Require(Permission.Command.Lodestone.List)
	fun list(@Sender sender: CommandSender) {
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Lodestones:<newline><lodestones></gray>",
			Placeholder.component("prefix", legacyText(SYSPREFIX)),
			Placeholder.component("lodestones",
				Component.join(JoinConfiguration.newlines(),
					lodestones.all()
						.sortedWith(compareBy({ !it.isPublic }, { it.name }))
						.map {
							miniMessage.deserialize("#<id> <name> <dark_gray><location></dark_gray> <public>",
								Placeholder.unparsed("id", it.id.toString()),
								Placeholder.unparsed("name", it.name),
								Placeholder.unparsed("location", it.location.toLocation().humanReadable()),
								Placeholder.unparsed("public", if (it.isPublic) "(public)" else ""))
								.color(if (it.isPublic) NamedTextColor.GOLD else NamedTextColor.GRAY)
						}
				)
			)
		))
	}

	@Command(aliases = ["destroy"], desc = "Destroy lodestone")
	@Require(Permission.Command.Lodestone.Destroy)
	fun destroy(@Sender sender: CommandSender, lodestone: Lodestone) {
		lodestones.destroy(lodestone)
		sender.sendMessage("$SYSPREFIX Destroyed lodestone #${lodestone.id} ${lodestone.name}")
	}

	@Command(aliases = ["create"], desc = "Create a lodestone")
	@Require(Permission.Command.Lodestone.Create)
	fun create(@Sender sender: Player, name: String) {
		val block = sender.getTargetBlock(10)
		if (block == null || block.type != Material.LODESTONE) {
			sender.sendError("Please aim at a lodestone block.")
			return
		}

		if (lodestones.nameExists(name)) {
			sender.sendError("The name '$name' is already taken. Please use another name.")
			return
		}

		val lodestone = lodestones.create(Lodestone(
			id = 0,
			location = block.location.toBlockIdentifier(),
			name,
			isPublic = false
		))

		sender.sendMessage("$SYSPREFIX Created lodestone #${lodestone.id}")
	}

	@Command(aliases = ["move"], desc = "Relocate a lodestone")
	@Require(Permission.Command.Lodestone.Move)
	fun move(@Sender sender: Player, lodestone: Lodestone) {
		val block = sender.getTargetBlock(10)
		if (block == null || block.type != Material.LODESTONE) {
			sender.sendError("Please aim at a lodestone block.")
			return
		}

		lodestone.location = block.location.toBlockIdentifier()
		sender.sendMessage("$SYSPREFIX Relocated lodestone #${lodestone.id}")
	}

	@Command(aliases = ["rename"], desc = "Rename a lodestone")
	@Require(Permission.Command.Lodestone.Rename)
	fun rename(@Sender sender: CommandSender, lodestone: Lodestone, name: String) {
		lodestone.name = name
		sender.sendMessage("$SYSPREFIX Renamed lodestone #${lodestone.id}")
	}

	@Command(aliases = ["setpublic"], desc = "Set whether a lodestone is public or private")
	@Require(Permission.Command.Lodestone.Rename)
	fun setpublic(@Sender sender: CommandSender, lodestone: Lodestone, value: Boolean) {
		lodestone.isPublic = value
		if (value) {
			sender.sendMessage("$SYSPREFIX lodestone #${lodestone.id} is now public")
		} else {
			sender.sendMessage("$SYSPREFIX lodestone #${lodestone.id} is now private")
		}
	}
}
