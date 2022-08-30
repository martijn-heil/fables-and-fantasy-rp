package com.fablesfantasyrp.plugin.chat.channel

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.logging.Logger


interface CommandSenderCompatibleChatChannel : ChatChannel {
	fun sendMessage(from: CommandSender, message: String)
	fun getRecipients(from: CommandSender): Sequence<CommandSender>
}

interface ToggleableChatChannel : ChatChannel

interface RawChatChannel : ChatChannel {
	fun sendMessage(from: Player, message: Component)
}

interface PreviewableChatChannel : ChatChannel {
	fun getPreview(from: Player, message: String): Component
}

interface SubChanneledChatChannel {
	fun resolveSubChannel(message: String): Pair<ChatChannel, String>
}

fun ChatChannel.resolveSubChannelRecursive(message: String): Pair<ChatChannel, String> {
	return if (this is SubChanneledChatChannel) {
		val resolved = this.resolveSubChannel(message)
		val content = resolved.second
		val subChannel = resolved.first
		if (subChannel is SubChanneledChatChannel && subChannel !== this) {
			subChannel.resolveSubChannelRecursive(content)
		} else {
			Pair(subChannel, content)
		}
	} else {
		Pair(this, message)
	}
}

interface PreviewableCommandSenderCompatibleChatChannel : ChatChannel {
	fun getPreview(from: CommandSender, message: String): Component
}

fun ChatChannel.Companion.fromString(s: String): ChatChannel? = ChatChannel.allStatic().find { it.toString() == s }

fun ChatChannel.Companion.allStatic(): Collection<ChatChannel> = listOf(
		ChatOutOfCharacter,
		ChatLocalOutOfCharacter,
		ChatInCharacter,
		ChatInCharacterStandard,
		ChatInCharacterShout,
		ChatInCharacterQuiet,
		ChatInCharacterWhisper,
		ChatInCharacterContextual,
		ChatSpectator,
		ChatStaff,
).plus(ChatStaff.subChannels)

fun ChatChannel.Companion.allNames(): Collection<String> = ChatChannel.allStatic().map { it.toString() }

fun ChatChannel.Companion.fromStringAliased(s: String): ChatChannel? {
	val name = s.lowercase()
	return when {
		name == "ooc" -> ChatOutOfCharacter
		name == "looc" -> ChatLocalOutOfCharacter
		Regex("(ic|rp)").matches(name) -> ChatInCharacter
		Regex("(ic|rp)[.#](whisper|w)").matches(name) -> ChatInCharacterWhisper
		Regex("(ic|rp)[.#](quiet|q)").matches(name) -> ChatInCharacterQuiet
		Regex("(ic|rp)[.#](shout|s)").matches(name) -> ChatInCharacterShout
		Regex("(ic|rp)[.#](contextual)").matches(name) -> ChatInCharacterContextual
		Regex("(spectator|sc|spectatorchat|specchat)").matches(name) -> ChatSpectator
		Regex("(staff|st|staffchat)").matches(name) -> ChatStaff
		Regex("(staff|st|staffchat)[.#].+").matches(name) -> {
			val subChannelName = Regex("(staff|st|staffchat)[.#](.+)").matchEntire(name)!!.groupValues[2].uppercase()
			ChatStaff.resolveSubChannelForName(subChannelName)
		}
		else -> null
	}
}

class ChatIllegalArgumentException(message: String) : IllegalArgumentException(message)



internal fun logChatToConsole(message: Component) {
	Bukkit.getConsoleSender().sendMessage(message)

	val plainText = PlainTextComponentSerializer.plainText().serialize(message)

	// Don't change the log level, it's significant. See ModerationLogHandler
	Logger.getLogger("FablesModerationLogger").finer(plainText)
}
