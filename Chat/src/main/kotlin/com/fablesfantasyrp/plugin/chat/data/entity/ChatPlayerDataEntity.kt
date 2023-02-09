package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.CHAT_CHAR
import com.fablesfantasyrp.plugin.chat.Permission
import com.fablesfantasyrp.plugin.chat.SYSPREFIX
import com.fablesfantasyrp.plugin.chat.channel.*
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.data.ChatPlayerData
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.form.completeWaitForChat
import com.fablesfantasyrp.plugin.knockout.knockout
import com.fablesfantasyrp.plugin.text.sendError
import me.neznamy.tab.api.TabAPI
import me.neznamy.tab.api.team.UnlimitedNametagManager
import net.kyori.adventure.text.format.Style
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import java.time.Instant
import java.util.*

class ChatPlayerDataEntity : ChatPlayerEntity, HasDirtyMarker<ChatPlayerEntity> {

	private val offlinePlayer: OfflinePlayer get() = Bukkit.getOfflinePlayer(id)

	override var channel: ChatChannel
		get() = when {
				!offlinePlayer.isWhitelisted -> ChatSpectator
				offlinePlayer.knockout.isKnockedOut -> ChatInCharacterQuiet
				else -> field
			}
		set(value) {
			if (field != value) {
				field = value
				dirtyMarker?.markDirty(this)
				offlinePlayer.player?.sendMessage("$SYSPREFIX Your chat channel has been switched to ${value.toString().uppercase()}!")
				if (value is ToggleableChatChannel && this.disabledChannels.contains(value)) {
					this.disabledChannels = this.disabledChannels.filter { it != value }.toSet()
				}
			}
		}

	override var chatStyle: Style?
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var disabledChannels: Set<ToggleableChatChannel> = emptySet()
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var isChatSpyEnabled: Boolean = false
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var chatSpyExcludeChannels: Set<ChatChannel> = emptySet()
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var isReceptionIndicatorEnabled: Boolean = false
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val id: UUID

	constructor(id: UUID,
				channel: ChatChannel,
				chatStyle: Style?,
				disabledChannels: Set<ToggleableChatChannel>,
				isReceptionIndicatorEnabled: Boolean,
				chatSpyEnabled: Boolean,
				chatSpyExcludeChannels: Set<ChatChannel>) {
		this.id = id
		this.channel = channel
		this.chatStyle = chatStyle
		this.disabledChannels = disabledChannels
		this.isReceptionIndicatorEnabled = isReceptionIndicatorEnabled
		this.isChatSpyEnabled = chatSpyEnabled
		this.chatSpyExcludeChannels = chatSpyExcludeChannels
	}

	override var dirtyMarker: DirtyMarker<ChatPlayerEntity>? = null

	override var isTyping: Boolean = false
		set(value) {
			if (value == field) return
			field = value
			val tabPlayer = TabAPI.getInstance().getPlayer(id) ?: return
			val teamManager = TabAPI.getInstance().teamManager as? UnlimitedNametagManager ?: return
			if (value) {
				teamManager.setLine(tabPlayer, "belowname", ChatColor.GRAY.toString() + ".")
				this.cycleTypingAnimation()
			} else {
				this.lastTypingAnimation = null
				this.previewChannel = null
				teamManager.resetLine(tabPlayer, "belowname")
			}
		}

	override var lastTimeTyping: Instant? = null
	override var lastTypingAnimation: String? = null
	override var previewChannel: ChatChannel? = null

	override fun mayChatIn(channel: ChatChannel): Boolean {
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")
		val permission = "${Permission.Channel.prefix}.${channel.toString().replace('#', '.')}"

		if (player.isWhitelisted && this.hasPermissionForChannel(channel) &&
				player.knockout.isKnockedOut && channel != ChatInCharacterQuiet) return false

		return (!player.isWhitelisted && channel == ChatSpectator) || player.hasPermission(permission)
	}

	override fun hasPermissionForChannel(channel: ChatChannel): Boolean {
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")
		val permission = "${Permission.Channel.prefix}.${channel.toString().replace('#', '.')}"
		return player.hasPermission(permission)
	}

	override fun doChat(message: String) {
		this.doChat(channel, message)
	}

	override fun doChat(rootChannel: ChatChannel, message: String) {
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")

		if (!this.mayChatIn(rootChannel)) {
			throw ChatIllegalArgumentException("You cannot chat in this chat channel")
		}

		val result: Pair<ChatChannel, String> = this.parseChatMessage(rootChannel, message)
		val channel = result.first
		val content = result.second

		if (!player.isWhitelisted && channel != ChatSpectator) {
			player.sendError("Permission denied.")
			return
		}

		if (!this.mayChatIn(channel)) {
			player.sendError("Permission denied.")
			return
		}

		if (content.isNotEmpty()) {
			if (channel is ToggleableChatChannel && this.disabledChannels.contains(channel)) {
				this.disabledChannels = this.disabledChannels.filter { it != channel }.toSet()
			}
			channel.sendMessage(player, content)
			player.completeWaitForChat()

			val chatSpyMessage = "${ChatColor.GRAY}[$channel] ${player.name}: $content"
			Bukkit.getOnlinePlayers()
					.filter {
						if (it == player) return@filter false
						if (!it.hasPermission(Permission.Command.ChatSpy)) return@filter false
						val data = it.chat
						data.hasPermissionForChannel(channel) && data.isChatSpyEnabled && !data.chatSpyExcludeChannels.contains(channel)
					}.forEach { it.sendMessage(chatSpyMessage) }
		} else {
			this.channel = channel
		}
	}


	override fun parseChatMessage(message: String): Pair<ChatChannel, String> {
		return this.parseChatMessage(this.channel, message)
	}

	override fun parseChatMessage(rootChannel: ChatChannel, message: String): Pair<ChatChannel, String> {
		val channelRegex = Regex("^\\s*\\$CHAT_CHAR([A-z.#]+)( (.*))?")
		val matchResult = channelRegex.matchEntire(message)
		return if (matchResult != null) {
			val content = matchResult.groupValues[3]
			val channelName = matchResult.groupValues[1]
			val channel = ChatChannel.fromStringAliased(channelName)
					?: throw ChatIllegalArgumentException("Unknown global channel '$channelName'.")
			channel.resolveSubChannelRecursive(content)
		} else {
			rootChannel.resolveSubChannelRecursive(message)
		}
	}

	override fun cycleTypingAnimation() {
		val animation = when (this.lastTypingAnimation) {
			"." -> ".."
			".." -> "..."
			"..." -> "...."
			"...." -> "."
			else -> "."
		}
		val tabPlayer = TabAPI.getInstance().getPlayer(id) ?: return
		val teamManager = TabAPI.getInstance().teamManager as? UnlimitedNametagManager ?: return
		teamManager.setLine(tabPlayer, "belowname", ChatColor.WHITE.toString() + animation)
		this.lastTypingAnimation = animation
	}


	override fun equals(other: Any?): Boolean = other is ChatPlayerData && other.id == this.id
	override fun hashCode(): Int = this.id.hashCode()
}
