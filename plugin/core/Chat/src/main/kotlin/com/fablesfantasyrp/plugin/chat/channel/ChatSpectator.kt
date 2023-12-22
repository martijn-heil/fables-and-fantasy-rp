package com.fablesfantasyrp.plugin.chat.channel


import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.text.Permission
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.Serializable

object ChatSpectator : ChatChannel, RawChatChannel, ToggleableChatChannel, CommandSenderCompatibleChatChannel,
		PreviewableChatChannel, Serializable {
	override fun getRecipients(from: Player) = this.getRecipients(from as CommandSender)

	override fun getRecipients(from: CommandSender) =
			Bukkit.getOnlinePlayers().asSequence()
					.filter {
						!it.isWhitelisted ||
								(it.hasPermission(Permission.Channel.Spectator) &&
										!it.chat.disabledChannels.contains(this))
					}
					.filter { !it.chat.disabledChannels.contains(this) }

	override suspend fun sendMessage(from: Player, message: String) = this.sendMessage(from, parseLinks(message))
	override fun sendMessage(from: CommandSender, message: String) = this.sendMessage(from, parseLinks(message))
	override suspend fun sendMessage(from: Player, message: Component) = this.sendMessage(from as CommandSender, message)

	fun sendMessage(from: CommandSender, message: Component) {
		val final = this.formatMessage(from, message)
		getRecipients(from).forEach { it.sendMessage(final) }
		logChatToConsole(final)
	}

	private fun formatMessage(from: CommandSender, message: Component): Component {
		val customResolver = TagResolver.builder()
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		return miniMessage.deserialize(
				"<click:run_command:/sc><green>[SC]</green></click> <gray><player_name></gray> <dark_gray>»</dark_gray> <yellow><message></yellow>",
				TagResolver.standard(), customResolver)
	}

	override suspend fun getPreview(from: Player, message: String): Component = this.formatMessage(from, parseLinks(message))
	override fun toString() = "spectator"
	fun readResolve(): Any? = ChatSpectator
	private const val serialVersionUID: Long = 1
}
