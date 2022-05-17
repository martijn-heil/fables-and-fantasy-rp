package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.gui.ChatColorGui
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import com.sk89q.intake.parametric.annotation.Text
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["ic", "rp"], desc = "Change to or type in IC chat.")
	@Require(Permission.Channel.Ic)
	fun ic(@Sender origin: Player, @Optional @Text message: String?) {
		val channel = ChatInCharacter
		val fPlayer = FablesPlayer.forPlayer(origin)
		when(message) {
			null -> fPlayer.chatChannel = channel
			else -> channel.sendMessage(fPlayer.player, message)
		}
	}

	@Command(aliases = ["looc"], desc = "Change to or type in LOOC chat.")
	@Require(Permission.Channel.Looc)
	fun looc(@Sender origin: Player, @Optional @Text message: String?) {
		val channel = ChatLocalOutOfCharacter
		val fPlayer = FablesPlayer.forPlayer(origin)
		when(message) {
			null -> fPlayer.chatChannel = channel
			else -> channel.sendMessage(fPlayer.player, message)
		}
	}

	@Command(aliases = ["ooc"], desc = "Change to or type in OOC chat.")
	@Require(Permission.Channel.Ooc)
	fun ooc(@Sender origin: Player, @Optional @Text message: String?) {
		val channel = ChatOutOfCharacter
		val fPlayer = FablesPlayer.forPlayer(origin)
		when(message) {
			null -> fPlayer.chatChannel = channel
			else -> channel.sendMessage(fPlayer.player, message)
		}
	}

	@Command(aliases = ["staffchat", "staff", "st"], desc = "Change to or type in staff chat.")
	@Require(Permission.Channel.Staff)
	fun staffchat(@Sender origin: Player, @Optional @Text message: String?) {
		val channel = ChatStaff
		val fPlayer = FablesPlayer.forPlayer(origin)
		when(message) {
			null -> fPlayer.chatChannel = channel
			else -> channel.sendMessage(fPlayer.player, message)
		}
	}

	@Command(aliases = ["spectatorchat", "specchat", "sc"], desc = "Change to or type in spectator chat.")
	@Require(Permission.Channel.Spectator)
	fun spectatorchat(@Sender origin: Player, @Optional @Text message: String?) {
		val channel = ChatSpectator
		val fPlayer = FablesPlayer.forPlayer(origin)
		when(message) {
			null -> fPlayer.chatChannel = channel
			else -> channel.sendMessage(fPlayer.player, message)
		}
	}

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
}
