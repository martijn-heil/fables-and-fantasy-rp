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
