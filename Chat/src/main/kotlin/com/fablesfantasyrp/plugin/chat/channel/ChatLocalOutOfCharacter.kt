package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import com.fablesfantasyrp.plugin.utils.ess
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import java.io.Serializable

object ChatLocalOutOfCharacter : ChatChannel, RawChatChannel, ToggleableChatChannel, PreviewableChatChannel, Serializable {
	override fun getRecipients(from: Player): Sequence<Player> =
			getPlayersWithinRange(from.location, 15U)
					.filter { !it.chat.disabledChannels.contains(this) }
					.filter { !it.ess.isIgnoredPlayer(from.ess) }

	override fun sendMessage(from: Player, message: String) {
		this.sendMessage(from, parseLinks(message))
	}

	override fun sendMessage(from: Player, message: Component) {
		if(PlainTextComponentSerializer.plainText().serialize(message).isEmpty()) return

		val final = this.formatMessage(from, message)
		val recipients = getRecipients(from).toList()
		recipients.forEach { it.sendMessage(final) }
		val loc = from.location
		logChatToConsole(Component.text()
				.append(Component.text("[${loc.blockX}, ${loc.blockY}, ${loc.blockZ}, ${loc.world.name}] "))
				.append(Component.text("${recipients.map { it.name }.sorted()} "))
				.append(final)
				.build())
	}

	private fun formatMessage(from: Player, message: Component): Component {
		val characterName = from.currentPlayerCharacter?.name ?: ""

		val customResolver = TagResolver.builder()
				.tag("character_name", Tag.selfClosingInserting(Component.text(characterName)))
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		return miniMessage.deserialize(
				"<yellow>[L]</yellow> <white><player_name></white> " +
						"<dark_gray>(</dark_gray><gray><character_name></gray><dark_gray>)</dark_gray> <yellow>»</yellow> " +
						"<gray><message></gray>", TagResolver.standard(), customResolver)
	}

	override fun getPreview(from: Player, message: String): Component = this.formatMessage(from, parseLinks(message))
	override fun toString() = "looc"
	fun readResolve(): Any? = ChatLocalOutOfCharacter
}
