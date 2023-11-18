package com.fablesfantasyrp.plugin.morelogging

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import java.util.logging.Level

class ModerationLogCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender !is ConsoleCommandSender) {
			sender.sendMessage(ChatColor.RED.toString() + "This command can only be used from the console.")
			return true
		}

		val level = try {
			args.getOrNull(0)?.uppercase()?.let { Level.parse(it) } ?: return true
		} catch(ex: IllegalArgumentException) {
			return true
		}
		val message = if (args.size > 1) args.slice(1 until args.size).joinToString(" ") else return true

		MODERATION_LOGGER.log(level, message)
		return true
	}
}
