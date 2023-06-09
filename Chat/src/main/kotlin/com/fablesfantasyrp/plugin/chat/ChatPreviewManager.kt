package com.fablesfantasyrp.plugin.chat

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.fablesfantasyrp.plugin.chat.channel.*
import com.fablesfantasyrp.plugin.form.currentChatInputForm
import com.fablesfantasyrp.plugin.text.formatError
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.time.Instant


class ChatPreviewManager(private val plugin: Plugin) {
	private val server: Server get() = plugin.server

	fun start() {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			server.onlinePlayers.forEach {
				val data = it.chat
				val now = Instant.now()
				if (data.isTyping && Duration.between(data.lastTimeTyping, now).toMillis() >= 4000) {
					data.isTyping = false
				}
			}
		}, 0, 1)

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			server.onlinePlayers.asSequence()
					.map { it.chat }
					.filter { it.isTyping }
					.forEach { it.cycleTypingAnimation() }
		}, 0, 10)

		ProtocolLibrary.getProtocolManager().addPacketListener(object : PacketAdapter(params().plugin(plugin)
				.listenerPriority(ListenerPriority.HIGHEST)
				.types(	PacketType.Login.Client.START,
						PacketType.Play.Client.CHAT_PREVIEW,
						PacketType.Play.Client.CHAT)) {

			override fun onPacketReceiving(event: PacketEvent) {
				if (event.isPlayerTemporary || event.isCancelled) return

				val player = event.player
				val chatPlayerEntity = player.chat
				val packet: PacketContainer = event.packet

				when (event.packetType) {
					PacketType.Play.Client.CHAT -> {
						event.isCancelled = true
						chatPlayerEntity.isTyping = false
						plugin.server.scheduler.scheduleSyncDelayedTask(plugin) {
							val chatMessage = packet.strings.read(0).take(256)

							val asyncChatEvent = AsyncPlayerChatEvent(false, player, chatMessage, server.onlinePlayers.toSet())
							plugin.server.pluginManager.callEvent(asyncChatEvent)

							val chatEvent = PlayerChatEvent(player, chatMessage)
							chatEvent.isCancelled = asyncChatEvent.isCancelled
							plugin.server.pluginManager.callEvent(chatEvent)
						}
					}

					PacketType.Play.Client.CHAT_PREVIEW -> {
						chatPlayerEntity.lastTimeTyping = Instant.now()
						chatPlayerEntity.isTyping = true
						val requestId = packet.integers.read(0)
						val rawChatMessage = packet.strings.read(0)
						try {
							val result: Pair<ChatChannel, String> = chatPlayerEntity.parseChatMessage(rawChatMessage)
							val message = result.second
							val channel = result.first
							val messageComponent = if (channel is PreviewableChatChannel && player.currentChatInputForm == null) {
								chatPlayerEntity.previewChannel = channel
								channel.getPreview(player, message)
							} else Component.text("")
							sendChatPreview(player, requestId, messageComponent)
						} catch (e: ChatIllegalArgumentException) {
							sendChatPreview(player, requestId, formatError(e.message ?: "Illegal argument."))
						} catch (e: ChatIllegalStateException) {
							sendChatPreview(player, requestId, formatError(e.message ?: "Illegal state."))
						} catch (e: ChatUnsupportedOperationException) {
							sendChatPreview(player, requestId, formatError(e.message ?: "Unsupported operation."))
						}
					}
				}
			}

			override fun onPacketSending(event: PacketEvent?) {
				// do nothing
			}
		})
	}

	fun sendChatPreview(player: Player, queryId: Int, channel: PreviewableChatChannel, message: String) {
		val adventureComponent = channel.getPreview(player, message)
		sendChatPreview(player, queryId, adventureComponent)
	}

	fun sendChatPreview(player: Player, queryId: Int, message: Component) {
		val previewPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT_PREVIEW)
		previewPacket.integers.write(0, queryId)

		val gsonComponent = GsonComponentSerializer.gson().serialize(message)
		val protocolLibComponent = WrappedChatComponent.fromJson(gsonComponent)

		previewPacket.chatComponents.write(0, protocolLibComponent)
		ProtocolLibrary.getProtocolManager().sendServerPacket(player, previewPacket)
	}
}
