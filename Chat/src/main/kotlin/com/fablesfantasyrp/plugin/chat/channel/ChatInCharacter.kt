package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.text.Permission
import com.fablesfantasyrp.plugin.text.formatChat
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.miniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import java.io.Serializable

object ChatInCharacter : ChatChannel, PreviewableChatChannel, SubChanneledChatChannel, Serializable {
	@Transient
	private val pattern = "^(\\*)?\\s*#([a-z|A-Z])\\s?(.*$)"

	override fun getRecipients(from: Player): Sequence<Player> = ChatInCharacterStandard.getRecipients(from)

	override fun sendMessage(from: Player, message: String) {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = resolved.first.let { if (it === this) ChatInCharacterStandard else it }
		channel.sendMessage(from, content)
	}

	private fun trimMessage(message: String): String {
		val matches = Regex(pattern).matchEntire(message)?.groupValues ?: return message
		return "${matches[1]}${matches[3]}"
	}

	override fun getPreview(from: Player, message: String): Component {
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

private fun startsWithAction(message: String) = Regex("^\\s*\\*.*").matches(message)
private fun stripLeadingStar(s: String) = Regex("^\\s*\\*\\s*(.*)").matchEntire(s)?.groupValues?.get(1) ?: s

abstract class AbstractChatInCharacter : ChatChannel, PreviewableChatChannel {
	abstract val range: UInt
	abstract val actionWord: String

	override fun getRecipients(from: Player): Sequence<Player> =
			getPlayersWithinRange(from.location, range)

	override fun sendMessage(from: Player, message: String) {
		if(message.isEmpty()) return

		val formatted = formatMessage(from, message) ?: return
		val recipients = getRecipients(from).toList()
		recipients.forEach { it.sendMessage(formatted) }
		val loc = from.location
		logChatToConsole(Component.text()
				.append(Component.text("[${loc.blockX}, ${loc.blockY}, ${loc.blockZ}, ${loc.world.name}] "))
				.append(Component.text("${recipients.map { it.name }.sorted()} "))
				.append(Component.text("(${from.name}) "))
				.append(formatted)
				.build())
	}

	protected open fun formatMessage(from: Player, message: String): Component? {
		val actionStyle = from.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW)
		val speechStyle = Style.style(NamedTextColor.WHITE)
		val characterName = from.currentPlayerCharacter?.name ?:
		throw IllegalStateException("Player without current character cannot chat in in-character chat.")

		val startsWithAction = startsWithAction(message)
		val finalMessage = message
				.let { stripLeadingStar(it) }
				.let { if(from.hasPermission(Permission.Format)) formatChat(it) else it }
				.let { alternateStyle(it.trim(), startsWithAction, actionStyle, speechStyle) }

		if(PlainTextComponentSerializer.plainText().serialize(finalMessage).isEmpty()) return null

		val customResolver = TagResolver.builder()
				.tag("character_name", Tag.selfClosingInserting(Component.text(characterName)
						.style(Style.style(NamedTextColor.YELLOW))))
				.tag("default_emote", Tag.selfClosingInserting(Component.text(if (!startsWithAction) "$actionWord " else "")
						.style(actionStyle)))
				.tag("message", Tag.selfClosingInserting(finalMessage))
				.build()
		return miniMessage.deserialize("<character_name> <default_emote><message>", TagResolver.standard(), customResolver)
	}

	override fun getPreview(from: Player, message: String): Component = this.formatMessage(from, message) ?: Component.text("")
}

object ChatInCharacterStandard : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = 15U

	@Transient
	override val actionWord = "says"

	override fun toString() = "ic.standard"
	fun readResolve(): Any? = ChatInCharacterStandard
}

object ChatInCharacterWhisper : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = 2U

	@Transient
	override val actionWord = "whispers"

	override fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[W] ").color(NamedTextColor.GRAY).append(it) }
	}

	override fun toString() = "ic.whisper"
	fun readResolve(): Any? = ChatInCharacterWhisper
}

object ChatInCharacterQuiet : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = 8U

	@Transient
	override val actionWord = "says quietly"

	override fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[Q] ").color(NamedTextColor.GRAY).append(it) }
	}

	override fun toString() = "ic.quiet"
	fun readResolve(): Any? = ChatInCharacterQuiet
}

object ChatInCharacterShout : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = 30U

	@Transient
	override val actionWord = "shouts"

	override fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[S] ").color(NamedTextColor.GRAY).append(it) }
	}

	override fun toString() = "ic.shout"
	fun readResolve(): Any? = ChatInCharacterShout
}

object ChatInCharacterContextual : AbstractChatInCharacter(), Serializable {
	@Transient
	override val range = 15U

	@Transient
	override val actionWord = ""

	override fun formatMessage(from: Player, message: String): Component {
		val customResolver = TagResolver.builder()
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name)))
				.tag("message", Tag.selfClosingInserting(Component.text(message)))
				.build()

		return miniMessage.deserialize("<gray>[</gray><yellow>!</yellow><gray>]</gray> <white><message></white> " +
				"<dark_gray>(</dark_gray><gray><player_name></gray><dark_gray>)</dark_gray>",
				TagResolver.standard(), customResolver)
	}

	override fun toString() = "ic.contextual"
	fun readResolve(): Any? = ChatInCharacterContextual
}
