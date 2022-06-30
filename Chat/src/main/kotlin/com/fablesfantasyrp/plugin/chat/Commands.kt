package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.gui.ChatColorGui
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import com.fablesfantasyrp.plugin.text.sendError
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["togglechat", "tc"], desc = "Toggle a chat on or off.")
	@Require(Permission.Command.Togglechat)
	fun togglechat(@Sender origin: Player, channel: ToggleableChatChannel) {
		val channelDisplayName = channel.toString().uppercase()

		val fPlayer = FablesPlayer.forPlayer(origin)
		val disabledChannels = fPlayer.disabledChatChannels
		fPlayer.disabledChatChannels = when {
			disabledChannels.contains(channel) -> {
				origin.sendMessage("$SYSPREFIX You have enabled $channelDisplayName chat.")
				disabledChannels.minus(channel)
			}
			else -> {
				origin.sendMessage("$SYSPREFIX You have disabled $channelDisplayName chat.")
				if (channel == fPlayer.chatChannel) {
					origin.sendMessage("$SYSPREFIX You have disabled your current channel, switching to LOOC instead.")
					fPlayer.chatChannel = ChatLocalOutOfCharacter
				}
				disabledChannels.plus(channel)
			}
		}
	}

	@Command(aliases = ["chatcolor", "ccolor"], desc = "Change your chat color.")
	@Require(Permission.Command.Chatcolor)
	fun chatcolor(@Sender sender: Player) {
		ChatColorGui(FablesChat.instance).show(sender)
	}

	open class AbstractChatChannelCommand(private val channel: ChatChannel, private val permission: String) : CommandExecutor {
		override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
			if (!sender.hasPermission(permission)) {
				sender.sendError("Permission denied.")
				return true
			}

			val message = args.joinToString(" ")
			if (channel !is CommandSenderCompatibleChatChannel && sender !is Player) {
				sender.sendError("You have to be a Player to use this command. You are a ${sender::class.java.simpleName}.")
				return true
			}

			try {
				if (message.isEmpty()) {
					if (sender !is Player) return false
					val fPlayer = FablesPlayer.forPlayer(sender)
					fPlayer.chatChannel = channel
				} else if (channel is CommandSenderCompatibleChatChannel) {
					channel.sendMessage(sender, message)
				} else {
					channel.sendMessage(sender as Player, message)
				}
			} catch (e: ChatIllegalArgumentException) {
				sender.sendError(e.message ?: "Illegal argument.")
			}

			return true
		}
	}

	class CommandChatLocalOutOfCharacter : AbstractChatChannelCommand(ChatLocalOutOfCharacter, Permission.Channel.Looc)
	class CommandChatSpectator : AbstractChatChannelCommand(ChatSpectator, Permission.Channel.Spectator)
	class CommandChatStaff : AbstractChatChannelCommand(ChatStaff, Permission.Channel.Staff)
	class CommandChatOutOfCharacter : AbstractChatChannelCommand(ChatOutOfCharacter, Permission.Channel.Ooc)
	class CommandChatInCharacter : AbstractChatChannelCommand(ChatInCharacter, Permission.Channel.Ic)
}
