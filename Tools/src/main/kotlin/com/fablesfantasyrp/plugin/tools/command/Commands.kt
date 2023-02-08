package com.fablesfantasyrp.plugin.tools.command

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.inventory.MirroredInventory
import com.fablesfantasyrp.plugin.inventory.MirroredInventoryManager
import com.fablesfantasyrp.plugin.inventory.inventory
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.profile.command.annotation.AllowPlayerName
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.tools.Permission
import com.fablesfantasyrp.plugin.tools.SYSPREFIX
import com.fablesfantasyrp.plugin.tools.command.provider.MinecraftTime
import com.fablesfantasyrp.plugin.utils.humanReadable
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class InventoryCommands(private val mirroredInventoryManager: MirroredInventoryManager) {
	@Command(aliases = ["invsee", "finvsee"], desc = "Invsee a character")
	@Require(Permission.Command.Invsee)
	fun invsee(@Sender sender: Player, @AllowCharacterName target: Profile) {
		val inventory = MirroredInventory(
				target.inventory.inventory,
				sender,
				Component.text("${target.id}'s inventory"))
		mirroredInventoryManager.register(inventory)

		sender.openInventory(inventory.bukkitInventory)
	}

	@Command(aliases = ["endersee", "enderchest", "echest", "fendersee", "fechest", "fenderchest"], desc = "Endersee a character")
	@Require(Permission.Command.Endersee)
	fun endersee(@Sender sender: Player, @AllowCharacterName target: Profile) {
		val inventory = MirroredInventory(
				target.inventory.enderChest,
				sender,
				Component.text("${target.id}'s enderchest"))
		mirroredInventoryManager.register(inventory)

		sender.openInventory(inventory.bukkitInventory)
	}
}

class Commands {
	@Command(aliases = ["teleport", "fteleport", "tp", "ftp", "tele", "ftele"], desc = "Teleport characters")
	@Require(Permission.Command.Teleport)
	fun teleport(@Sender sender: CommandSender,
				 @AllowCharacterName @AllowPlayerName one: Profile,
				 @Optional @AllowCharacterName @AllowPlayerName two: Profile?) {
		if (two != null) {
			one.location = two.location
		} else if (sender is Player) {
			sender.teleport(one.location)
		} else {
			sender.sendError("You have to be a player to use this command.")
		}
	}

	@Command(aliases = ["tppos"], desc = "Teleport to a position")
	@Require(Permission.Command.Tppos)
	fun tppos(@Sender sender: CommandSender,
			  to: Location,
			  @CommandTarget(Permission.Command.Tppos + ".others") @AllowCharacterName @AllowPlayerName target: Profile) {
		target.location = to
		sender.sendMessage("$SYSPREFIX Teleported #${target.id} to ${to.humanReadable()}")
	}

	@Command(aliases = ["tphere", "ftphere"], desc = "Teleport characters to you")
	@Require(Permission.Command.Tphere)
	fun tphere(@Sender sender: Player, @AllowCharacterName who: Profile) {
		who.location = sender.location
	}

	inner class Ptime {
		@Command(aliases = ["set"], desc = "")
		@Require(Permission.Command.Ptime)
		fun set(@Sender sender: CommandSender, time: MinecraftTime, @CommandTarget(Permission.Command.Ptime + ".others") target: Player) {
			target.setPlayerTime(time.ticks, false)
			sender.sendMessage("$SYSPREFIX Set ${target.name}'s playertime to ${time.ticks}")
		}

		@Command(aliases = ["reset"], desc = "")
		@Require(Permission.Command.Ptime)
		fun reset(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Ptime + ".others") target: Player) {
			target.resetPlayerTime()
			sender.sendMessage("$SYSPREFIX Reset ${target.name}'s playertime")
		}
	}

	@Command(aliases = ["rigcheer"], desc = "Rig the cheer")
	@Require(Permission.Command.Rigcheer)
	fun rigcheer(@Sender sender: CommandSender, target: Player) {}
}
