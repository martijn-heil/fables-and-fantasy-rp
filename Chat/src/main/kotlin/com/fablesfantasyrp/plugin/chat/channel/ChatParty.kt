package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.party.PartySpectatorManager
import com.fablesfantasyrp.plugin.party.data.entity.Party
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toNamedTextColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChatParty(val party: Party) : ChatChannel, PreviewableChatChannel, CommandSenderCompatibleChatChannel {
	private val profileManager = Services.get<ProfileManager>()
	private val partySpectatorManager = Services.get<PartySpectatorManager>()
	private val server = Services.get<Server>()

	override fun sendMessage(from: CommandSender, message: String) {
		if (party.isDestroyed) {
			throw ChatIllegalStateException("${party.name} has been disbanded, please chat in another channel.")
		}

		val formattedMessage = formatMessage(from, message)
		getRecipients(from).forEach { it.sendMessage(formattedMessage) }
	}

	override fun getRecipients(from: CommandSender): Sequence<CommandSender> {
		return party.members.asSequence()
			.mapNotNull { profileManager.getCurrentForProfile(it.profile) }
			.plus(partySpectatorManager.getSpectators(party))
			.plus(server.consoleSender)
	}

	override fun getPreview(from: Player, message: String): Component = formatMessage(from, message)
	override fun sendMessage(from: Player, message: String) = sendMessage(from as CommandSender, message)
	override fun getRecipients(from: Player): Sequence<Player>
		= party.members.asSequence().mapNotNull { profileManager.getCurrentForProfile(it.profile) }

	private fun formatMessage(from: CommandSender, message: String): Component {
		val isStaff = from is Player && partySpectatorManager.getParty(from) != null
		val nameStyle = if (isStaff) Style.style(NamedTextColor.RED) else Style.style(NamedTextColor.GRAY)

		return miniMessage.deserialize("<gold>[<party>]</gold> <gray><name></gray> <dark_gray>»</dark_gray> <color:#77b5fe><message></color:#77b5fe>",
			Placeholder.component("party", Component.text("PARTY")
				.color(party.color?.chatColor?.toNamedTextColor() ?: NamedTextColor.GRAY)),
			Placeholder.component("name", Component.text(from.name).style(nameStyle)),
			Placeholder.unparsed("message", message)
		)
	}

	override fun toString() = "PARTY"
}
