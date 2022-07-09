package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.chat.disabledChatChannels
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import com.fablesfantasyrp.plugin.text.*
import com.fablesfantasyrp.plugin.utils.ess
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor.translateAlternateColorCodes
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.Serializable

object ChatOutOfCharacter : ChatChannel, RawChatChannel, ToggleableChatChannel, CommandSenderCompatibleChatChannel,
		PreviewableChatChannel, Serializable {
	override fun getRecipients(from: Player) =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { !it.ess.isIgnoredPlayer(from.ess) }
					.filter { !FablesPlayer.forPlayer(it).disabledChatChannels.contains(this) }

	override fun getRecipients(from: CommandSender) =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { !FablesPlayer.forPlayer(it).disabledChatChannels.contains(this) }

	override fun sendMessage(from: Player, message: String) = this.sendMessage(from, parseLinks(message))
	override fun sendMessage(from: Player, message: Component) = this.sendMessage(from as CommandSender, message)
	override fun sendMessage(from: CommandSender, message: String) = this.sendMessage(from, parseLinks(message))

	fun sendMessage(from: CommandSender, message: Component) {
		if(PlainTextComponentSerializer.plainText().serialize(message).isEmpty()) return

		val final = this.formatMessage(from, message)
		getRecipients(from).forEach { it.sendMessage(final) }
		logChatToConsole(final)
	}

	override fun getPreview(from: Player, message: String): Component = this.formatMessage(from, message)

	private fun formatMessage(from: CommandSender, message: String): Component = this.formatMessage(from, parseLinks(message))

	private fun formatMessage(from: CommandSender, message: Component): Component {
		val chatPrefix = if (from is Player) {
			vaultChat.getPlayerPrefix(from)
					.let { translateAlternateColorCodes('&', it) }
					.let { legacyText(it) }
		} else Component.text()

		val chatSuffix = if (from is Player) {
			vaultChat.getPlayerSuffix(from)
					.let { translateAlternateColorCodes('&', it) }
					.let { legacyText(it) }
		} else Component.text()

		val customResolver = TagResolver.builder()
				.tag("prefix", Tag.selfClosingInserting(chatPrefix))
				.tag("suffix", Tag.selfClosingInserting(chatSuffix))
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name).style(from.nameStyle)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		return miniMessage.deserialize(
				"<gold>[G]</gold> <gray><prefix><player_name><suffix></gray> <dark_gray>»</dark_gray> <gray><message></gray>",
				TagResolver.standard(), customResolver)
	}

	override fun toString() = "ooc"
	fun readResolve(): Any? = ChatOutOfCharacter
}
