package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender

fun sendWelcomeMessage(who: CommandSender) {
	val message = miniMessage.deserialize(
			"<gray>" +
					"Welcome to <green><bold>Fables & Fantasy</bold></green>!<newline>" +
					"<newline>" +
					"In order to play, you must apply for <yellow>Whitelist Access</yellow>!<newline>" +
					"This is a <yellow>very quick and simple</yellow> process!<newline>" +
					"Your application will be reviewed <yellow>in 1 day or less</yellow>.<newline>" +
					"<whitelist_application_link><newline>" +
					"<newline>" +
					"If you have <yellow>any questions</yellow>, please feel free to ask on our <yellow>Discord</yellow>!<newline>" +
					"<discord_link><newline>" +
					"<yellow>Type in chat</yellow> to talk with other spectators and staff.<newline>" +
					"Please enjoy your stay in the world of Eden!" +
					"</gray>",
			Placeholder.component("whitelist_application_link",
					parseLinks("https://forums.fablesfantasyrp.com/index.php?form/whitelist-application.1/select")),
			Placeholder.component("discord_link", parseLinks("https://discord.gg/ymNFxDKPx9"))
	)
	who.sendMessage(message)
}
