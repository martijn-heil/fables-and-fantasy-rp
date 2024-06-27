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

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.broadcastToPermissionLevel
import com.fablesfantasyrp.plugin.utils.getPermissionLevel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Server
import org.bukkit.command.CommandSender

class StaffActionBroadcasterImpl(private val server: Server) : StaffActionBroadcaster {
	override fun log(by: CommandSender, message: Component) {
		val formattedMessage = miniMessage.deserialize("<gray><italic>[<by>: <message>]</italic></gray>",
			Placeholder.unparsed("by", by.name),
			Placeholder.component("message", message)
		)

		val byLevel = by.getPermissionLevel(Permission.Notices.Level, 3)
		server.broadcastToPermissionLevel(Permission.Notices.CanSee, 3, byLevel, formattedMessage)
	}
}
