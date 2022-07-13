package com.fablesfantasyrp.plugin.chat.channel

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


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
		if (subChannel is SubChanneledChatChannel) {
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

fun ChatChannel.Companion.fromString(s: String): ChatChannel? = when(s.lowercase()) {
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

fun ChatChannel.Companion.all(): Collection<String> = listOf("ooc", "looc", "ic", "ic.whisper", "ic.quiet", "ic.shout", "staff", "spectator")

fun ChatChannel.Companion.fromStringAliased(s: String): ChatChannel? {
	val name = s.lowercase()
	return when {
		name == "ooc" -> ChatOutOfCharacter
		name == "looc" -> ChatLocalOutOfCharacter
		Regex("(ic|rp)").matches(name) -> ChatInCharacter
		Regex("(ic|rp)\\.(whisper|w)").matches(name) -> ChatInCharacterWhisper
		Regex("(ic|rp)\\.(quiet|q)").matches(name) -> ChatInCharacterQuiet
		Regex("(ic|rp)\\.(shout|s)").matches(name) -> ChatInCharacterShout
		Regex("(spectator|sc|spectatorchat|specchat)").matches(name) -> ChatInCharacter
		Regex("(staff|st|staffchat)").matches(name) -> ChatStaff
		else -> null
	}
}

fun ChatChannel.toStringAliased(): String? = when (this) {
		is ChatOutOfCharacter -> "ooc"
		is ChatInCharacter -> "ic.standard"
		is ChatInCharacterStandard -> "ic.standard"
		else -> null
	}

class ChatIllegalArgumentException(message: String) : IllegalArgumentException(message)



internal fun logChatToConsole(message: Component) {
	Bukkit.getConsoleSender().sendMessage(message)
}
