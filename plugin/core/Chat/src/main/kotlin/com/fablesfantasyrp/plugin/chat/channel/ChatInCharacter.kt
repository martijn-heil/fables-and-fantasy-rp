package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.Permission
import com.fablesfantasyrp.plugin.text.formatChat
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.DISTANCE_QUIET
import com.fablesfantasyrp.plugin.utils.DISTANCE_SHOUT
import com.fablesfantasyrp.plugin.utils.DISTANCE_TALK
import com.fablesfantasyrp.plugin.utils.DISTANCE_WHISPER
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.koin.core.context.GlobalContext
import java.io.Serializable

object ChatInCharacter : ChatChannel, PreviewableChatChannel, SubChanneledChatChannel, Serializable {
	@Transient
	private val pattern = "^(\\*)?\\s*#([a-z|A-Z])\\s?(.*$)"

	override fun getRecipients(from: Player): Sequence<Player> = ChatInCharacterStandard.getRecipients(from)

	override suspend fun sendMessage(from: Player, message: String) {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = resolved.first.let { if (it === this) ChatInCharacterStandard else it }
		channel.sendMessage(from, content)
	}

	private fun trimMessage(message: String): String {
		val matches = Regex(pattern).matchEntire(message)?.groupValues ?: return message
		return "${matches[1]}${matches[3]}"
	}

	override suspend fun getPreview(from: Player, message: String): Component {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = (resolved.first as? PreviewableChatChannel)?.let { if (it === this) ChatInCharacterStandard else it }
				?: return Component.text("")
		return channel.getPreview(from, content)
	}

	override fun resolveSubChannel(message: String): Pair<ChatChannel, String> {
		val match = Regex(pattern).matchEntire(message)

		return when {
			message.startsWith("<<") -> Pair(ChatInCharacterContextual, message.removePrefix("<<").trimStart())
			match != null -> {
				when(val channel = match.groups[2]!!.value.lowercase()) {
					"w" -> Pair(ChatInCharacterWhisper, trimMessage(message))
					"q" -> Pair(ChatInCharacterQuiet, trimMessage(message))
					"s" -> Pair(ChatInCharacterShout, trimMessage(message))
					else -> throw ChatIllegalArgumentException("Unknown relative channel '$channel'.")
				}
			}
			else -> Pair(this, message)
		}
	}

	override fun toString() = "ic"
	fun readResolve(): Any? = ChatInCharacter
	private const val serialVersionUID: Long = 1
}

private fun alternateStyle(message: String, startsWithAction: Boolean, actionStyle: Style, speechStyle: Style): Component {
	var action = startsWithAction
	return Component.text().append(message.split('"')
			.asSequence()
			.map { it.trim() }
			.filter { it.isNotEmpty() }
			.map {
				when (action) {
					true -> Component.text(it).style(actionStyle)
					false -> Component.text("\"$it\"").style(speechStyle)
				}.let { s -> action = !action; s }
			}.join(Component.text(" ")).asIterable()).asComponent().compact()
}

private fun startsWithAction(message: String) = Regex("^\\s*\\*\\s*[^\"\\s]+.*").matches(message)
private fun stripLeadingStar(s: String) = Regex("^\\s*\\*\\s*(.*)").matchEntire(s)?.groupValues?.get(1) ?: s

abstract class AbstractChatInCharacter : ChatChannel, PreviewableChatChannel {
	abstract val range: UInt
	abstract val actionWord: String
	private val profileManager: ProfileManager = GlobalContext.get().get()
	private val characters: CharacterRepository = GlobalContext.get().get()

	override fun getRecipients(from: Player): Sequence<Player> =
			getPlayersWithinRange(from.location, range)

	override suspend fun sendMessage(from: Player, message: String) {
		if(message.isEmpty()) return

		val formatted = formatMessage(from, message) ?: return
		val recipients = getRecipients(from).toList()
		recipients.forEach { it.sendMessage(formatted) }
		val loc = from.location
		logChatToConsole(Component.text()
				.append(Component.text("[${loc.humanReadable()}] "))
				.append(Component.text("${recipients.map { it.name }.sorted()} "))
				.append(Component.text("(${from.name}) "))
				.append(formatted)
				.build())
	}


	protected open suspend fun formatMessage(from: Player, message: String): Component? = formatMessage(from, message, false)

	private suspend fun formatMessage(from: Player, message: String, preview: Boolean): Component? {
		val actionStyle = from.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW)
		val speechStyle = Style.style(NamedTextColor.WHITE)
		val profile = profileManager.getCurrentForPlayer(from)
		val character = profile?.let { characters.forProfile(it) }
		val characterName = character?.name ?:
			throw ChatIllegalStateException("Player without current character cannot chat in in-character chat.")

		val startsWithAction = startsWithAction(message)
		val finalMessage = message
				.let { stripLeadingStar(it) }
				.let { if(from.hasPermission(Permission.Format)) formatChat(it) else it }
				.let { alternateStyle(it.trim(), startsWithAction, actionStyle, speechStyle) }

		val plainTextFinalMessage = PlainTextComponentSerializer.plainText().serialize(finalMessage)
		if(plainTextFinalMessage.isEmpty()) return null
		val insertActionWord = !startsWithAction && !stripLeadingStar(message).trim().startsWith('"')

		val characterNameComponent = Component.text(characterName).style(Style.style(NamedTextColor.YELLOW))
		val characterNameHoverComponent = miniMessage.deserialize("<dark_gray><character_name> is played by <gray><player_name></gray></dark_gray>",
			Placeholder.unparsed("character_name", characterName),
			Placeholder.unparsed("player_name", from.name)
		)

		return miniMessage.deserialize("<character_name> <default_emote><message>",
			Placeholder.component("character_name",
				if (preview) characterNameComponent
				else characterNameComponent.hoverEvent(HoverEvent.showText(characterNameHoverComponent))
			),
			Placeholder.component("default_emote", (Component.text(if (insertActionWord) "$actionWord " else "").style(actionStyle))),
			Placeholder.component("message", (finalMessage))
		)
	}

	override suspend fun getPreview(from: Player, message: String): Component
		= this.formatMessage(from, message, true) ?: Component.text("")
}

object ChatInCharacterStandard : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = DISTANCE_TALK

	@Transient
	override val actionWord = "says"

	override fun toString() = "ic#standard"
	fun readResolve(): Any? = ChatInCharacterStandard
	private const val serialVersionUID: Long = 1
}

object ChatInCharacterWhisper : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = DISTANCE_WHISPER

	@Transient
	override val actionWord = "whispers"

	override suspend fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[W] ").color(NamedTextColor.GRAY).append(it) }
	}

	override fun toString() = "ic#whisper"
	fun readResolve(): Any? = ChatInCharacterWhisper
	private const val serialVersionUID: Long = 1
}

object ChatInCharacterQuiet : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = DISTANCE_QUIET

	@Transient
	override val actionWord = "says quietly"

	override suspend fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[Q] ").color(NamedTextColor.GRAY).append(it) }
	}

	override fun toString() = "ic#quiet"
	fun readResolve(): Any? = ChatInCharacterQuiet
	private const val serialVersionUID: Long = 1
}

object ChatInCharacterShout : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = DISTANCE_SHOUT

	@Transient
	override val actionWord = "shouts"

	override suspend fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[S] ").color(NamedTextColor.GRAY).append(it) }
	}

	override fun toString() = "ic#shout"
	fun readResolve(): Any? = ChatInCharacterShout
	private const val serialVersionUID: Long = 1
}

object ChatInCharacterContextual : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = DISTANCE_TALK

	@Transient
	override val actionWord = ""

	override suspend fun formatMessage(from: Player, message: String): Component {
		val customResolver = TagResolver.builder()
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name)))
				.tag("message", Tag.selfClosingInserting(Component.text(message)))
				.build()

		return miniMessage.deserialize("<gray>[</gray><yellow>!</yellow><gray>]</gray> <white><message></white> " +
				"<dark_gray>(</dark_gray><gray><player_name></gray><dark_gray>)</dark_gray>",
				TagResolver.standard(), customResolver)
	}

	override fun toString() = "ic#contextual"
	fun readResolve(): Any? = ChatInCharacterContextual
	private const val serialVersionUID: Long = 1
}
