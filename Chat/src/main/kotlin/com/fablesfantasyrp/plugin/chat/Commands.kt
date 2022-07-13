package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.channel.*
import com.fablesfantasyrp.plugin.chat.gui.ChatColorGui
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.asEnabledDisabledComponent
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.util.auth.AuthorizationException
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["togglechat", "tc"], desc = "Toggle a chat on or off.")
	@Require(Permission.Command.Togglechat)
	fun togglechat(@Sender sender: Player, channel: ToggleableChatChannel) {
		val channelDisplayName = channel.toString().uppercase()

		val chatPlayerEntity = sender.chat
		val disabledChannels = chatPlayerEntity.disabledChannels
		chatPlayerEntity.disabledChannels = when {
			disabledChannels.contains(channel) -> {
				sender.sendMessage("$SYSPREFIX You have enabled $channelDisplayName chat.")
				disabledChannels.minus(channel)
			}
			else -> {
				sender.sendMessage("$SYSPREFIX You have disabled $channelDisplayName chat.")
				if (channel == chatPlayerEntity.channel) {
					sender.sendMessage("$SYSPREFIX You have disabled your current channel, switching to LOOC instead.")
					chatPlayerEntity.channel = ChatLocalOutOfCharacter
				}
				disabledChannels.plus(channel)
			}
		}
	}

	@Command(aliases = ["togglereceptionindicator"], desc = "Toggle your chat reception indicator.")
	@Require(Permission.Command.Togglechatindicator)
	fun togglereceptionindicator(@Sender sender: Player) {
		val chatPlayerData = sender.chat
		chatPlayerData.isReceptionIndicatorEnabled = !chatPlayerData.isReceptionIndicatorEnabled
		val newStateDescription = chatPlayerData.isReceptionIndicatorEnabled.asEnabledDisabledComponent()
		sender.sendMessage(legacyText(SYSPREFIX)
				.append(Component.text(" Chat reception indicators are now ").color(NamedTextColor.GRAY))
				.append(newStateDescription))
	}

	@Command(aliases = ["chatchannel", "channel", "chan", "ch"], desc = "Change your chat channel.")
	@Require(Permission.Command.Chatchannel)
	fun chatchannel(@Sender sender: Player, channel: ChatChannel) {
		if (!sender.hasPermission("fables.chat.channel.${channel}")) throw AuthorizationException()
		sender.chat.channel = channel
	}

	@Command(aliases = ["chatcolor", "ccolor"], desc = "Change your chat color.")
	@Require(Permission.Command.Chatcolor)
	fun chatcolor(@Sender sender: Player) {
		ChatColorGui(FablesChat.instance).show(sender)
	}

	open class AbstractChatChannelCommand(private val channel: ChatChannel, private val permission: String) : CommandExecutor {
		override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
			fun playErrorSound() {
				if (sender is Player) {
					sender.playSound(Sound.sound(
							Key.key("minecraft:block.anvil.land"), Sound.Source.BLOCK, 1.0f, 1.0f))
				}
			}

			if (!sender.hasPermission(permission)) {
				sender.sendError("Permission denied.")
				return true
			}

			val message = args.joinToString(" ")
			if (channel !is CommandSenderCompatibleChatChannel && sender !is Player) {
				sender.sendError("You have to be a Player to use this command. You are a ${sender::class.java.simpleName}.")
				return true
			} else if (message.isNotEmpty()) {
				sender.sendMessage(
						miniMessage.deserialize("<red>" +
								"<bold>Using</bold> /<label> <bold>to send chat messages in chat channels is no longer supported.</bold><newline>" +
								"<bold>Please write</bold> <green><prefix><label></green> <bold>in chat instead.</bold>" +
								"</red>",
								Placeholder.unparsed("label", label),
								Placeholder.unparsed("prefix", CHAT_CHAR)
						)
				)
				playErrorSound()
			} else {
				sender.sendMessage(
						miniMessage.deserialize("<red>" +
										"<bold>Using</bold> /<label> <bold>to switch your chat channel to <channel> " +
										"is no longer supported.</bold><newline>" +
										"<bold>Please write </bold><green><prefix><label></green><bold> in chat instead.</bold>" +
										"</red>",
								Placeholder.unparsed("label", label),
								Placeholder.unparsed("channel", channel.toString()),
								Placeholder.unparsed("prefix", CHAT_CHAR)
						)
				)
				playErrorSound()
			}

			try {
				if (message.isEmpty()) {
					if (sender !is Player) return false
					sender.chat.channel = channel
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
