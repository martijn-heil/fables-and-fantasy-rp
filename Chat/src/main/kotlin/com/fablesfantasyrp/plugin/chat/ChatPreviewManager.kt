package com.fablesfantasyrp.plugin.chat

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import com.fablesfantasyrp.plugin.text.formatError
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.plugin.Plugin


class ChatPreviewManager(private val plugin: Plugin) {
	init {
		ProtocolLibrary.getProtocolManager().addPacketListener(object : PacketAdapter(params().plugin(plugin)
				.listenerPriority(ListenerPriority.HIGHEST)
				.types(	PacketType.Login.Client.START,
						PacketType.Play.Client.CHAT_PREVIEW,
						PacketType.Play.Client.CHAT)) {

			override fun onPacketReceiving(event: PacketEvent) {
				if (event.isPlayerTemporary || event.isCancelled) return

				val player = event.player
				val fPlayer = FablesPlayer.forPlayer(player)
				val packet: PacketContainer = event.packet

				when (event.packetType) {
					PacketType.Play.Client.CHAT -> {
						event.isCancelled = true
						plugin.server.scheduler.scheduleSyncDelayedTask(plugin) {
							val chatMessage = packet.strings.read(0).take(256)
							val chatEvent = PlayerChatEvent(player, chatMessage)
							plugin.server.pluginManager.callEvent(chatEvent)
						}
					}

					PacketType.Play.Client.CHAT_PREVIEW -> {
						val requestId = packet.integers.read(0)
						val rawChatMessage = packet.strings.read(0)
						try {
							val result: Pair<ChatChannel, String> = fPlayer.parseChatMessage(rawChatMessage)
							val message = result.second
							val channel = result.first as? PreviewableChatChannel ?: return
							val messageComponent = channel.getPreview(player, message)
							sendChatPreview(player, requestId, messageComponent)
						} catch (e: ChatIllegalArgumentException) {
							sendChatPreview(player, requestId, formatError(e.message ?: "Illegal argument."))
							return
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
