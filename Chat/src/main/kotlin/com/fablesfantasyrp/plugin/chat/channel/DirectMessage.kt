package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DirectMessageRoot(from: CommandSender) : StatefulTreeChatChannel(from) {

}

class DirectMessage(val from: CommandSender, val to: CommandSender) : ChatChannel, CommandSenderCompatibleChatChannel {
	override fun sendMessage(from: CommandSender, message: String) {
		from.sendMessage(miniMessage.deserialize("<gold>[<me> -> <to>]</gold> <gray><message></gray>",
				Placeholder.unparsed("message", message),
				Placeholder.component("me", Component.text("me").style(from.nameStyle)),
				Placeholder.component("to", Component.text(to.name).style(to.nameStyle))
		))

		to.sendMessage(miniMessage.deserialize("<gold>[<from> -> <me>]</gold> <gray><message></gray>",
				Placeholder.unparsed("message", message),
				Placeholder.component("me", Component.text("me").style(to.nameStyle)),
				Placeholder.component("from", Component.text(from.name).style(from.nameStyle))
		))
	}

	override fun sendMessage(from: Player, message: String) = this.sendMessage(from as CommandSender, message)
	override fun getRecipients(from: CommandSender) = sequenceOf(this.from, to)
	override fun getRecipients(from: Player) = sequenceOf(this.from as? Player, to as? Player).filterNotNull()
	override fun toString() = "dm#${to.name.lowercase()}"
}
