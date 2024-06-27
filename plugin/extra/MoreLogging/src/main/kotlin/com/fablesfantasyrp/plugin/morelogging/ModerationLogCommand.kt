/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
