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
package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChatDirectMessageRoot(from: CommandSender) : StatefulTreeChatChannel(from) {

}

class ChatDirectMessage(val from: CommandSender, val to: CommandSender) :
		ChatChannel,
		CommandSenderCompatibleChatChannel,
		PreviewableChatChannel,
		PreviewableCommandSenderCompatibleChatChannel {
	override fun sendMessage(from: CommandSender, message: String) {
		val fromPrefix = miniMessage.deserialize("<gold>[<me> -> <to>]</gold>",
				Placeholder.component("me", Component.text("me").style(from.nameStyle)),
				Placeholder.component("to", Component.text(to.name).style(to.nameStyle))
		).clickEvent(ClickEvent.runCommand("/dm ${to.name}"))

		from.sendMessage(miniMessage.deserialize("<prefix> <gray><message></gray>",
				Placeholder.unparsed("message", message),
				Placeholder.unparsed("to_name", to.name),
				Placeholder.component("prefix", fromPrefix)
		))

		val toPrefix = miniMessage.deserialize("<gold>[<from> -> <me>]</gold>",
				Placeholder.component("me", Component.text("me").style(to.nameStyle)),
				Placeholder.component("from", Component.text(from.name).style(from.nameStyle))
		).clickEvent(ClickEvent.runCommand("/dm ${from.name}"))
		to.sendMessage(miniMessage.deserialize(
				"<prefix> <gray><message></gray>",
				Placeholder.unparsed("message", message),
				Placeholder.unparsed("from_name", from.name),
				Placeholder.component("prefix", toPrefix)
		))
	}

	override fun getPreview(from: CommandSender, message: String): Component {
		return miniMessage.deserialize("<gold>[<me> -> <to>]</gold> <gray><message></gray>",
				Placeholder.unparsed("message", message),
				Placeholder.component("me", Component.text("me").style(from.nameStyle)),
				Placeholder.component("to", Component.text(to.name).style(to.nameStyle))
		)
	}

	override suspend fun getPreview(from: Player, message: String): Component = this.getPreview(from as CommandSender, message)
	override suspend fun sendMessage(from: Player, message: String) = this.sendMessage(from as CommandSender, message)
	override fun getRecipients(from: CommandSender) = sequenceOf(this.from, to)
	override fun getRecipients(from: Player) = sequenceOf(this.from as? Player, to as? Player).filterNotNull()
	override fun toString() = "dm#${to.name.lowercase()}"
}
