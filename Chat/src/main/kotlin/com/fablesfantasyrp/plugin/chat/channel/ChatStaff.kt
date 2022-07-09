package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.text.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.Serializable

object ChatStaff : ChatChannel, RawChatChannel, CommandSenderCompatibleChatChannel, PreviewableChatChannel, Serializable {
	override fun getRecipients(from: Player): Sequence<Player> =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { it.hasPermission(Permission.Channel.Staff) }

	override fun getRecipients(from: CommandSender): Sequence<Player> =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { it.hasPermission(Permission.Channel.Staff) }

	override fun sendMessage(from: Player, message: String) = this.sendMessage(from, parseLinks(message))
	override fun sendMessage(from: CommandSender, message: String) = this.sendMessage(from, parseLinks(message))
	override fun sendMessage(from: Player, message: Component) = sendMessage(from as CommandSender, message)

	fun sendMessage(from: CommandSender, message: Component) {
		if(PlainTextComponentSerializer.plainText().serialize(message).isEmpty()) return

		val final = this.formatMessage(from, message)
		getRecipients(from).forEach { it.sendMessage(final) }
		logChatToConsole(final)
	}

	private fun formatMessage(from: CommandSender, message: Component): Component {
		val chatPrefix = if (from is Player) {
			vaultChat.getPlayerPrefix(from)
					.let { ChatColor.translateAlternateColorCodes('&', it) }
					.let { legacyText(it) }
		} else Component.text()

		val chatSuffix = if (from is Player) {
			vaultChat.getPlayerSuffix(from)
					.let { ChatColor.translateAlternateColorCodes('&', it) }
					.let { legacyText(it) }
		} else Component.text()

		val customResolver = TagResolver.builder()
				.tag("prefix", Tag.selfClosingInserting(chatPrefix))
				.tag("suffix", Tag.selfClosingInserting(chatSuffix))
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name).style(from.nameStyle)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		return miniMessage.deserialize(
				"<dark_red>[ST]</dark_red> <gray><prefix><player_name><suffix></gray> <dark_gray>»</dark_gray> <red><message></red>",
				TagResolver.standard(), customResolver)
	}

	override fun getPreview(from: Player, message: String): Component = this.formatMessage(from, parseLinks(message))
	override fun toString() = "staff"
	fun readResolve(): Any? = ChatStaff
}
