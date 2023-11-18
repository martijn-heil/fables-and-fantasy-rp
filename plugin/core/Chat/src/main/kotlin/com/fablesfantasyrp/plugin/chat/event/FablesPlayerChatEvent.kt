package com.fablesfantasyrp.plugin.chat.event

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import org.bukkit.command.CommandSender
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FablesChatEvent(val sender: CommandSender,
					  val channel: ChatChannel,
					  val content: String,
					  val recipients: Set<CommandSender>) : Event(), Cancellable {
	override fun getHandlers(): HandlerList = Companion.handlers

	private var isCancelled = false
	override fun isCancelled() = this.isCancelled
	override fun setCancelled(cancel: Boolean) { this.isCancelled = cancel }

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
