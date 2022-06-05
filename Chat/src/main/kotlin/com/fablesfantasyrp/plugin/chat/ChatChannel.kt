package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.chat.miniMessage
import com.fablesfantasyrp.plugin.chat.vaultChat
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import com.fablesfantasyrp.plugin.text.*
import com.fablesfantasyrp.plugin.utils.ess
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor.translateAlternateColorCodes
import org.bukkit.entity.Player


interface ChatChannel {
	fun getRecipients(from: Player): Sequence<Player>

	@Throws(IllegalArgumentException::class)
	fun sendMessage(from: Player, message: String)

	companion object
}

interface ToggleableChatChannel

interface RawChatChannel {
	fun sendMessage(from: Player, message: Component)
}

fun ChatChannel.Companion.fromString(s: String) = when(s.lowercase()) {
	"ooc" -> ChatOutOfCharacter
	"looc" -> ChatLocalOutOfCharacter
	"ic" -> ChatInCharacter
	"ic.whisper" -> ChatInCharacterWhisper
	"ic.quiet" -> ChatInCharacterQuiet
	"ic.shout" -> ChatInCharacterShout
	"staff" -> ChatStaff
	"spectator" -> ChatSpectator
	else -> null
}

object ChatOutOfCharacter : ChatChannel, RawChatChannel, ToggleableChatChannel {
	override fun getRecipients(from: Player) =
		Bukkit.getOnlinePlayers().asSequence()
				.filter { !it.ess.isIgnoredPlayer(from.ess) }
				.filter { !FablesPlayer.forPlayer(it).disabledChatChannels.contains(this) }

	override fun sendMessage(from: Player, message: String) {
		this.sendMessage(from, parseLinks(message))
	}

	override fun sendMessage(from: Player, message: Component) {
		val fPlayer = FablesPlayer.forPlayer(from)

		val legacyChatPrefix = vaultChat.getPlayerPrefix(from)
		val chatPrefix = legacyChatPrefix
				.let { translateAlternateColorCodes('&', it) }
				.let { legacyText(it) }
		val playerNameStyle = fPlayer.playerNameStyle

		val chatSuffix = vaultChat.getPlayerSuffix(from)
				.let { translateAlternateColorCodes('&', it) }
				.let { legacyText(it) }

		val customResolver = TagResolver.builder()
				.tag("prefix", Tag.selfClosingInserting(chatPrefix))
				.tag("suffix", Tag.selfClosingInserting(chatSuffix))
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name).style(playerNameStyle)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		val final = miniMessage.deserialize(
				"<gold>[G]</gold> <gray><prefix><player_name><suffix></gray> <dark_gray>»</dark_gray> <gray><message></gray>",
				TagResolver.standard(), customResolver)
		getRecipients(from).forEach { it.sendMessage(final) }
		logChatToConsole(final)
	}

	override fun toString() = "ooc"
}

object ChatLocalOutOfCharacter : ChatChannel, RawChatChannel {
	override fun getRecipients(from: Player): Sequence<Player> =
			getPlayersWithinRange(from.location, 15U)
					.filter { !it.ess.isIgnoredPlayer(from.ess) }

	override fun sendMessage(from: Player, message: String) {
		this.sendMessage(from, parseLinks(message))
	}

	override fun sendMessage(from: Player, message: Component) {
		val fPlayer = FablesPlayer.forPlayer(from)
		val characterName = fPlayer.currentPlayerCharacter?.name ?: ""

		val customResolver = TagResolver.builder()
				.tag("character_name", Tag.selfClosingInserting(Component.text(characterName)))
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		val final = miniMessage.deserialize(
				"<yellow>[L]</yellow> <white><player_name></white> " +
						"<dark_gray>(</dark_gray><gray><character_name></gray><dark_gray>)</dark_gray> <yellow>»</yellow> " +
						"<gray><message></gray>", TagResolver.standard(), customResolver)
		val recipients = getRecipients(from).toList()
		recipients.forEach { it.sendMessage(final) }
		val loc = from.location
		logChatToConsole(Component.text()
				.append(Component.text("[${loc.blockX}, ${loc.blockY}, ${loc.blockZ}, ${loc.world.name}] "))
				.append(Component.text("${recipients.map { it.name }.sorted()} "))
				.append(final)
				.build())
	}

	override fun toString() = "looc"
}

object ChatSpectator : ChatChannel, RawChatChannel, ToggleableChatChannel {
	override fun getRecipients(from: Player) =
			Bukkit.getOnlinePlayers().asSequence()
					.filter {
						!it.isWhitelisted ||
							(it.hasPermission(Permission.Channel.Spectator) &&
									!FablesPlayer.forPlayer(it).disabledChatChannels.contains(this))
					}
					.filter { !FablesPlayer.forPlayer(it).disabledChatChannels.contains(this) }

	override fun sendMessage(from: Player, message: String) {
		this.sendMessage(from, parseLinks(message))
	}

	override fun sendMessage(from: Player, message: Component) {
		val customResolver = TagResolver.builder()
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		val final = miniMessage.deserialize(
				"<green>[SC]</green> <gray><player_name></gray> <dark_gray>»</dark_gray> <yellow><message></yellow>",
				TagResolver.standard(), customResolver)
		getRecipients(from).forEach { it.sendMessage(final) }
		logChatToConsole(final)
	}

	override fun toString() = "spectator"
}

object ChatStaff : ChatChannel, RawChatChannel {
	override fun getRecipients(from: Player): Sequence<Player> =
			Bukkit.getOnlinePlayers().asSequence()
					.filter { it.hasPermission(Permission.Channel.Staff) }

	override fun sendMessage(from: Player, message: String) {
		this.sendMessage(from, parseLinks(message))
	}

	override fun sendMessage(from: Player, message: Component) {
		val fPlayer = FablesPlayer.forPlayer(from)

		val legacyChatPrefix = vaultChat.getPlayerPrefix(from)
		val chatPrefix = legacyChatPrefix
				.let { translateAlternateColorCodes('&', it) }
				.let { legacyText(it) }
		val playerNameStyle = fPlayer.playerNameStyle

		val chatSuffix = vaultChat.getPlayerSuffix(from)
				.let { translateAlternateColorCodes('&', it) }
				.let { legacyText(it) }

		val customResolver = TagResolver.builder()
				.tag("prefix", Tag.selfClosingInserting(chatPrefix))
				.tag("suffix", Tag.selfClosingInserting(chatSuffix))
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name).style(playerNameStyle)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		val final = miniMessage.deserialize(
				"<dark_red>[ST]</dark_red> <gray><prefix><player_name><suffix></gray> <dark_gray>»</dark_gray> <red><message></red>",
				TagResolver.standard(), customResolver)
		getRecipients(from).forEach { it.sendMessage(final) }
		logChatToConsole(final)
	}

	override fun toString() = "staff"
}

object ChatInCharacter : ChatChannel {
	private val pattern = "^(\\*)?\\s*#([a-z|A-Z])\\s?(.*$)"

	override fun getRecipients(from: Player): Sequence<Player> {
		throw NotImplementedError()
	}

	override fun sendMessage(from: Player, message: String) =
			getRelativeChannel(message).sendMessage(from, trimMessage(message))

	private fun trimMessage(message: String): String {
		val matches = Regex(pattern).matchEntire(message)?.groupValues ?: return message
		return "${matches[1]}${matches[3]}"
	}

	private fun getRelativeChannel(message: String): ChatChannel {
		val match = Regex(pattern).matchEntire(message)

		return when {
			message.startsWith("<<") -> ChatInCharacterContextual
			match != null -> {
				when(val channel = match.groups[2]!!.value.lowercase()) {
					"w" -> ChatInCharacterWhisper
					"q" -> ChatInCharacterQuiet
					"s" -> ChatInCharacterShout
					else -> throw IllegalArgumentException("Unknown relative channel '$channel'.")
				}
			}
			else -> ChatInCharacterStandard
		}
	}

	override fun toString() = "ic"
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

abstract class AbstractChatInCharacter : ChatChannel {
	abstract val range: UInt
	abstract val actionWord: String

	override fun getRecipients(from: Player): Sequence<Player> =
			getPlayersWithinRange(from.location, range)

	override fun sendMessage(from: Player, message: String) {
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
		val fPlayer = FablesPlayer.forPlayer(from)
		val actionStyle = fPlayer.chatStyle ?: Style.style(NamedTextColor.YELLOW)
		val speechStyle = Style.style(NamedTextColor.WHITE)
		val characterName = fPlayer.currentPlayerCharacter?.name ?:
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

	override fun toString() = "ic"
}

object ChatInCharacterStandard : AbstractChatInCharacter() {
	override val range = 15U
	override val actionWord = "says"
}

object ChatInCharacterWhisper : AbstractChatInCharacter() {
	override val range = 2U
	override val actionWord = "whispers"

	override fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[W] ").color(NamedTextColor.GRAY).append(it) }
	}
}

object ChatInCharacterQuiet : AbstractChatInCharacter() {
	override val range = 8U
	override val actionWord = "says quietly"

	override fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[Q] ").color(NamedTextColor.GRAY).append(it) }
	}
}

object ChatInCharacterShout : AbstractChatInCharacter() {
	override val range = 30U
	override val actionWord = "shouts"

	override fun formatMessage(from: Player, message: String): Component? {
		return super.formatMessage(from, message)
				?.let { Component.text("[S] ").color(NamedTextColor.GRAY).append(it) }
	}
}

object ChatInCharacterContextual : AbstractChatInCharacter() {
	override val range = 15U
	override val actionWord = ""

	override fun formatMessage(from: Player, message: String): Component {
		val customResolver = TagResolver.builder()
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name)))
				.tag("message", Tag.selfClosingInserting(Component.text(message.removePrefix("<<").trim())))
				.build()

		return miniMessage.deserialize("<gray>[</gray><yellow>!</yellow><gray>]</gray> <white><message></white> " +
				"<dark_gray>(</dark_gray><gray><player_name></gray><dark_gray>)</dark_gray>",
				TagResolver.standard(), customResolver)
	}
}

private fun logChatToConsole(message: Component) {
	Bukkit.getConsoleSender().sendMessage(message)
}
