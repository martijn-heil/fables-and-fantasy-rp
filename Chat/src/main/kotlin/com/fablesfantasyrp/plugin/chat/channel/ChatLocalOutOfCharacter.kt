package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import com.fablesfantasyrp.plugin.utils.humanReadable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.koin.core.context.GlobalContext
import java.io.Serializable

object ChatLocalOutOfCharacter : ChatChannel, RawChatChannel, ToggleableChatChannel, PreviewableChatChannel, Serializable {
	private val profileManager: ProfileManager = GlobalContext.get().get()
	private val characters: CharacterRepository = GlobalContext.get().get()

	override fun getRecipients(from: Player): Sequence<Player> =
			getPlayersWithinRange(from.location, 15U)
					.filter { !it.chat.disabledChannels.contains(this) }

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
				.append(Component.text("[${loc.humanReadable()}] "))
				.append(Component.text("${recipients.map { it.name }.sorted()} "))
				.append(final)
				.build())
	}

	private fun formatMessage(from: Player, message: Component): Component {
		val profile = profileManager.getCurrentForPlayer(from)
		val character = profile?.let { characters.forProfile(it) }
		val characterName = character?.name ?: ""

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
	private const val serialVersionUID: Long = 1
}
