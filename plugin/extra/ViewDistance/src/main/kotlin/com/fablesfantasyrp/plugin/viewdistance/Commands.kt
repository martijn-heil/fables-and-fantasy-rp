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
package com.fablesfantasyrp.plugin.viewdistance

import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.CommandException
import com.fablesfantasyrp.caturix.Require
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["setviewdistance"], desc = "Set a player's view distance")
	@Require("fables.viewdistance.command.setviewdistance")
	fun setviewdistance(distance: Int, @CommandTarget("fables.viewdistance.command.setviewdistance.others") target: Player) {
		if (distance < 1 || distance > 32) throw CommandException("Please enter a distance between 1 and 32 chunks.")
		target.viewDistance = distance // This is a Paper Bukkit API extension
	}
}
