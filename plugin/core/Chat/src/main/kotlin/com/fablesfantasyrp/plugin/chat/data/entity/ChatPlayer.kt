package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.CHAT_CHAR
import com.fablesfantasyrp.plugin.chat.Permission
import com.fablesfantasyrp.plugin.chat.SYSPREFIX
import com.fablesfantasyrp.plugin.chat.channel.*
import com.fablesfantasyrp.plugin.chat.event.FablesChatEvent
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
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

class ChatPlayer : DataEntity<UUID, ChatPlayer>, HasDirtyMarker<ChatPlayer> {

	private val offlinePlayer: OfflinePlayer get() = Bukkit.getOfflinePlayer(id)

	var channel: ChatChannel
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

	var chatStyle: Style?
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	var disabledChannels: Set<ToggleableChatChannel> = emptySet()
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	var isChatSpyEnabled: Boolean = false
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	var chatSpyExcludeChannels: Set<ChatChannel> = emptySet()
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	var isReceptionIndicatorEnabled: Boolean = false
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val id: UUID

	constructor(id: UUID,
				channel: ChatChannel = ChatOutOfCharacter,
				chatStyle: Style? = null,
				disabledChannels: Set<ToggleableChatChannel> = setOf(ChatSpectator),
				isReceptionIndicatorEnabled: Boolean = false,
				isChatSpyEnabled: Boolean = false,
				chatSpyExcludeChannels: Set<ChatChannel> = emptySet(),
				dirtyMarker: DirtyMarker<ChatPlayer>? = null) {
		this.id = id
		this.channel = channel
		this.chatStyle = chatStyle
		this.disabledChannels = disabledChannels
		this.isReceptionIndicatorEnabled = isReceptionIndicatorEnabled
		this.isChatSpyEnabled = isChatSpyEnabled
		this.chatSpyExcludeChannels = chatSpyExcludeChannels
		this.dirtyMarker = dirtyMarker // must be last
	}

	override var dirtyMarker: DirtyMarker<ChatPlayer>? = null

	var isTyping: Boolean = false
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

	var lastTimeTyping: Instant? = null
	var lastTypingAnimation: String? = null
	var previewChannel: ChatChannel? = null

	fun mayChatIn(channel: ChatChannel): Boolean {
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")
		val permission = "${Permission.Channel.prefix}.${channel.toString().replace('#', '.')}"

		if (player.isWhitelisted && this.hasPermissionForChannel(channel) &&
				player.knockout.isKnockedOut && channel != ChatInCharacterQuiet) return false

		return (!player.isWhitelisted && channel == ChatSpectator) || player.hasPermission(permission)
	}

	fun hasPermissionForChannel(channel: ChatChannel): Boolean {
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")
		val permission = "${Permission.Channel.prefix}.${channel.toString().replace('#', '.')}"
		return player.hasPermission(permission)
	}

	suspend fun doChat(message: String) {
		this.doChat(channel, message)
	}

	suspend fun doChat(rootChannel: ChatChannel, message: String) {
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")

		if (!this.mayChatIn(rootChannel)) {
			throw ChatIllegalArgumentException("You cannot chat in this chat channel")
		}

		val result: Pair<ChatChannel, String> = this.parseChatMessage(rootChannel, message, true)
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
			val recipients = channel.getRecipients(player).toHashSet()
			if (!FablesChatEvent(player, channel, content, recipients).callEvent()) return
			channel.sendMessage(player, content)
			player.completeWaitForChat()
		} else {
			this.channel = channel
		}
	}


	suspend fun parseChatMessage(message: String): Pair<ChatChannel, String> {
		return this.parseChatMessage(this.channel, message)
	}

	suspend fun parseChatMessage(rootChannel: ChatChannel, message: String): Pair<ChatChannel, String>
		= parseChatMessage(rootChannel, message, false)

	suspend fun parseChatMessage(rootChannel: ChatChannel, message: String, updateState: Boolean): Pair<ChatChannel, String> {
		val channelRegex = Regex("^\\s*\\$CHAT_CHAR(\\.?[A-z0-9_.#]+)( (.*))?")
		val matchResult = channelRegex.matchEntire(message)
		return if (matchResult != null) {
			val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is offline")
			val content = matchResult.groupValues[3]
			val channelName = matchResult.groupValues[1]
			val channel = ChatChannel.fromStringAliased(channelName, player)
					?: throw ChatIllegalArgumentException("Unknown global channel '$channelName'.")
			channel.resolveSubChannelRecursive(content, updateState)
		} else {
			rootChannel.resolveSubChannelRecursive(message, updateState)
		}
	}

	fun cycleTypingAnimation() {
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


	override fun equals(other: Any?): Boolean = other is ChatPlayer && other.id == this.id
	override fun hashCode(): Int = this.id.hashCode()
}
