package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.party.PartySpectatorManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.Services
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.logging.Logger
import kotlin.reflect.KClass


interface CommandSenderCompatibleChatChannel : ChatChannel {
	fun sendMessage(from: CommandSender, message: String)
	fun getRecipients(from: CommandSender): Sequence<CommandSender>
}

interface ToggleableChatChannel : ChatChannel

interface RawChatChannel : ChatChannel {
	suspend fun sendMessage(from: Player, message: Component)
}

interface PreviewableChatChannel : ChatChannel {
	suspend fun getPreview(from: Player, message: String): Component
}

interface SubChanneledChatChannel {
	fun resolveSubChannel(message: String): Pair<ChatChannel, String>
}

fun ChatChannel.resolveSubChannelRecursive(message: String, updateState: Boolean = false): Pair<ChatChannel, String> {
	return if (this is SubChanneledChatChannel) {
		val resolved = this.resolveSubChannel(message)
		val content = resolved.second
		val subChannel = resolved.first
		if (subChannel !== this && updateState && this is StatefulTreeChatChannel) {
			this.lastSubChannel = subChannel
		}

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
).plus(ChatStaff.subChannels.values)

fun ChatChannel.Companion.allNames(): Collection<String> = ChatChannel.allStatic().map { it.toString() }

private val statefulTreeChannels = HashMap<CommandSender, HashMap<KClass<*>, StatefulTreeChatChannel>>()

suspend fun ChatChannel.Companion.fromStringAliased(s: String, from: CommandSender): ChatChannel? {
	val name = s.lowercase()

	val profileManager = Services.get<ProfileManager>()
	val partySpectatorManager = Services.get<PartySpectatorManager>()
	val characters = Services.get<CharacterRepository>()
	val character = (from as? Player)?.let { profileManager.getCurrentForPlayer(it) }?.let { characters.forProfile(it) }
	val parties = Services.get<PartyRepository>()
	val party = character?.let { parties.forMember(it) } ?: (from as? Player)?.let { partySpectatorManager.getParty(it) }

	return when {
		name == "ooc" -> ChatOutOfCharacter
		name == "looc" -> ChatLocalOutOfCharacter
		Regex("(ic|rp)").matches(name) -> ChatInCharacter
		Regex("(ic|rp)[.#](whisper|w)").matches(name) -> ChatInCharacterWhisper
		Regex("(ic|rp)[.#](quiet|q)").matches(name) -> ChatInCharacterQuiet
		Regex("(ic|rp)[.#](shout|s)").matches(name) -> ChatInCharacterShout
		Regex("(ic|rp)[.#](contextual)").matches(name) -> ChatInCharacterContextual
		Regex("(spectator|sc|spectatorchat|specchat)").matches(name) -> ChatSpectator
		party != null && Regex("(party|pc|partychat)").matches(name) -> ChatParty(party)
		Regex("(staff|st|staffchat)").matches(name) -> ChatStaff
		Regex("(staff|st|staffchat)[.#].+").matches(name) -> {
			val subChannelName = Regex("(staff|st|staffchat)[.#](.+)").matchEntire(name)!!.groupValues[2].uppercase()
			ChatStaff.resolveSubChannelForName(subChannelName)
		}
		Regex("(dm|directmessage|msg|r)").matches(name) -> {
			statefulTreeChannels
					.computeIfAbsent(from) { HashMap() }
					.computeIfAbsent(ChatDirectMessageRoot::class) { ChatDirectMessageRoot(from) }
		}
		Regex("(dm|directmessage|msg|r)[.#].+").matches(name) -> {
			val subChannelName = Regex("(dm|directmessage|msg|r)[.#](.+)").matchEntire(name)!!.groupValues[2].uppercase()
			ChatDirectMessageRoot(from).resolveSubChannelForName(subChannelName)
		}
		else -> null
	}
}

class ChatIllegalArgumentException(message: String) : IllegalArgumentException(message)
class ChatIllegalStateException(message: String) : IllegalStateException(message)
class ChatUnsupportedOperationException(message: String) : UnsupportedOperationException(message)



internal fun logChatToConsole(message: Component) {
	Bukkit.getConsoleSender().sendMessage(message)

	val plainText = PlainTextComponentSerializer.plainText().serialize(message)

	// Don't change the log level, it's significant. See ModerationLogHandler
	Logger.getLogger("FablesModerationLogger").finer(plainText)
}
