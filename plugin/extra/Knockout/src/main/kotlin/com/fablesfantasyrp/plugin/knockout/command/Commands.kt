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
package com.fablesfantasyrp.plugin.knockout.command

import com.fablesfantasyrp.plugin.knockout.Permission
import com.fablesfantasyrp.plugin.knockout.knockout
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class Commands {
	@Command(aliases = ["knockout", "ko"], desc = "Knock out a player")
	@Require(Permission.Command.Knockout)
	fun knockout(target: Player, @CommandTarget by: Player?) {
		val knockoutPlayer = target.knockout
		if (!knockoutPlayer.isKnockedOut) knockoutPlayer.knockout(EntityDamageEvent.DamageCause.CUSTOM, by)
	}

	@Command(aliases = ["revive"], desc = "Knock out a player")
	@Require(Permission.Command.Revive)
	fun revive(target: Player, @CommandTarget by: Player?) {
		val knockoutPlayer = target.knockout
		if (knockoutPlayer.isKnockedOut) knockoutPlayer.revive(by)
	}
}
