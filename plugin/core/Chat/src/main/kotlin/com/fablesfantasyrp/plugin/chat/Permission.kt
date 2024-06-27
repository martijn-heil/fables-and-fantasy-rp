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
package com.fablesfantasyrp.plugin.chat

object Permission {
	const val prefix = "fables.chat"
	const val SpectatorDuty = "$prefix.spectatorduty"

	object Channel {
		const val prefix = Permission.prefix + ".channel"
		const val Ic = "${prefix}.ic"
		const val Ooc = "${prefix}.ooc"
		const val Looc = "${prefix}.looc"
		const val Spectator = "${prefix}.spectator"
		const val Staff = "${prefix}.staff"
		const val Party = "${prefix}.party"
	}

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Togglechat = "${prefix}.togglechat"
		const val Chatcolor = "${prefix}.chatcolor"
		const val Chatchannel = "${prefix}.chatchannel"
		const val Togglereceptionindicator = "${prefix}.togglereceptionindicator"
		const val ChatSpy = "${prefix}.chatspy"
	}

	object Exempt {
		private const val prefix = Permission.prefix + ".exempt"
		const val ChatSpy = "${prefix}.chatspy"
	}
}
