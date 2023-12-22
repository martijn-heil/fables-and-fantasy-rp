package com.fablesfantasyrp.plugin.chat.channel


import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.text.*
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
					.filter { !it.chat.disabledChannels.contains(this) }

	override fun getRecipients(from: CommandSender) =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { !it.chat.disabledChannels.contains(this) }

	override suspend fun sendMessage(from: Player, message: String) = this.sendMessage(from, parseLinks(message))
	override suspend fun sendMessage(from: Player, message: Component) = this.sendMessage(from as CommandSender, message)
	override fun sendMessage(from: CommandSender, message: String) = this.sendMessage(from, parseLinks(message))

	fun sendMessage(from: CommandSender, message: Component) {
		if(PlainTextComponentSerializer.plainText().serialize(message).isEmpty()) return

		val final = this.formatMessage(from, message)
		getRecipients(from).forEach { it.sendMessage(final) }
		logChatToConsole(final)
	}

	override suspend fun getPreview(from: Player, message: String): Component = this.formatMessage(from, message)

	private fun formatMessage(from: CommandSender, message: String): Component = this.formatMessage(from, parseLinks(message))

	private fun formatMessage(from: CommandSender, message: Component): Component {
		val chatPrefix = if (from is Player) from.prefix else Component.text()

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
				"<click:run_command:/ooc><gold>[G]</gold></click> <gray><prefix><player_name><suffix></gray> <dark_gray>»</dark_gray> <gray><message></gray>",
				TagResolver.standard(), customResolver)
	}

	override fun toString() = "ooc"
	fun readResolve(): Any? = ChatOutOfCharacter
	private const val serialVersionUID: Long = 1
}
