package com.fablesfantasyrp.plugin.tools

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.inventory.MirroredInventory
import com.fablesfantasyrp.plugin.inventory.MirroredInventoryManager
import com.fablesfantasyrp.plugin.inventory.inventory
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.text.sendError
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class InventoryCommands(private val mirroredInventoryManager: MirroredInventoryManager) {
	@Command(aliases = ["invsee", "finvsee"], desc = "Invsee a character")
	@Require(Permission.Command.Invsee)
	fun invsee(@Sender sender: Player, @AllowCharacterName target: PlayerInstance) {
		val inventory = MirroredInventory(
				target.inventory.inventory,
				sender,
				Component.text("${target.id}'s inventory"))
		mirroredInventoryManager.register(inventory)

		sender.openInventory(inventory.bukkitInventory)
	}

	@Command(aliases = ["endersee", "enderchest", "echest", "fendersee", "fechest", "fenderchest"], desc = "Endersee a character")
	@Require(Permission.Command.Endersee)
	fun endersee(@Sender sender: Player, @AllowCharacterName target: PlayerInstance) {
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
	fun teleport(@Sender sender: CommandSender, @AllowCharacterName one: PlayerInstance, @Optional @AllowCharacterName two: PlayerInstance?) {
		if (two != null) {
			one.location = two.location
		} else if (sender is Player) {
			sender.teleport(one.location)
		} else {
			sender.sendError("You have to be a player to use this command.")
		}
	}

	@Command(aliases = ["tphere", "ftphere"], desc = "Teleport characters")
	@Require(Permission.Command.Tphere)
	fun tphere(@Sender sender: Player, @AllowCharacterName who: PlayerInstance) {
		who.location = sender.location
	}
}
