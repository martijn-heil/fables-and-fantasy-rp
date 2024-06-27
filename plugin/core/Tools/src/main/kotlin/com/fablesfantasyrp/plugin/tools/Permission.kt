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
package com.fablesfantasyrp.plugin.tools

object Permission {
	const val prefix = "fables.tools"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Invsee = "$prefix.invsee"
		const val Endersee = "$prefix.endersee"
		const val ClearInventory = "$prefix.clearinventory"
		const val ClearEnderChest = "$prefix.clearenderchest"
		const val Teleport = "$prefix.teleport"
		const val Tppos = "$prefix.tppos"
		const val Tphere = "$prefix.tphere"
		const val Ptime = "$prefix.ptime"
		const val PWeather = "$prefix.pweather"
		const val GameMode = "$prefix.gamemode"
		const val PowerTool = "$prefix.powertool"
		const val Fly = "$prefix.fly"
		const val Speed = "$prefix.speed"
		const val God = "$prefix.god"
		const val Seen = "$prefix.seen"
		const val Whois = "$prefix.whois"
		const val Back = "$prefix.back"
		const val Spawn = "$prefix.spawn"
		const val SetCustomModelData = "$prefix.setcustommodeldata"
		const val BottleColor = "$prefix.bottlecolor"
		const val ExportItem = "$prefix.exportitem"
		const val Rigcheer = "$prefix.tphere"
	}
}
