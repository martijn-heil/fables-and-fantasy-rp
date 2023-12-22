package com.fablesfantasyrp.plugin.chat.command

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.chat.FablesChat
import com.fablesfantasyrp.plugin.chat.Permission
import com.fablesfantasyrp.plugin.chat.SYSPREFIX
import com.fablesfantasyrp.plugin.chat.channel.*
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.event.FablesChatEvent
import com.fablesfantasyrp.plugin.chat.gui.ChatColorGui
import com.fablesfantasyrp.plugin.party.PartySpectatorManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.asEnabledDisabledComponent
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.util.auth.AuthorizationException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

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
					val newChannel = if (chatPlayerEntity.channel != ChatLocalOutOfCharacter) {
						ChatLocalOutOfCharacter
					} else {
						ChatInCharacter
					}

					sender.sendMessage("$SYSPREFIX You have disabled your current channel, switching to $newChannel instead.")
					chatPlayerEntity.channel = newChannel
				}
				disabledChannels.plus(channel)
			}
		}
	}

	@Command(aliases = ["togglereceptionindicator"], desc = "Toggle your chat reception indicator.")
	@Require(Permission.Command.Togglereceptionindicator)
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

	inner class ChatSpy {
		@Command(aliases = ["on"], desc = "Enable your chat spy")
		@Require(Permission.Command.ChatSpy)
		fun on(@Sender sender: CommandSender, @CommandTarget(Permission.Command.ChatSpy + ".others") target: Player) {
			target.chat.isChatSpyEnabled = true
			sender.sendMessage(miniMessage.deserialize("<gray><prefix> <value> chatspy for <player></gray>",
					Placeholder.component("prefix", legacyText(SYSPREFIX)),
					Placeholder.component("value", true.asEnabledDisabledComponent()),
					Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
			))
		}

		@Command(aliases = ["off"], desc = "Disable your chat spy")
		@Require(Permission.Command.ChatSpy)
		fun off(@Sender sender: CommandSender, @CommandTarget(Permission.Command.ChatSpy + ".others") target: Player) {
			target.chat.isChatSpyEnabled = false
			sender.sendMessage(miniMessage.deserialize("<gray><prefix> <value> chatspy for <player></gray>",
					Placeholder.component("prefix", legacyText(SYSPREFIX)),
					Placeholder.component("value", false.asEnabledDisabledComponent()),
					Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
			))
		}

		inner class Excludes {
			@Command(aliases = ["add"], desc = "Add a channel to your chat spy excluded channels list")
			@Require(Permission.Command.ChatSpy)
			fun add(@Sender sender: Player, channel: ChatChannel) {
				val data = sender.chat
				data.chatSpyExcludeChannels = data.chatSpyExcludeChannels.plus(channel)
				sender.sendMessage("$SYSPREFIX Added '$channel' to your chat spy excluded channels list")
			}

			@Command(aliases = ["remove"], desc = "Remove a channel from your chat spy excluded channels list")
			@Require(Permission.Command.ChatSpy)
			fun remove(@Sender sender: Player, channel: ChatChannel) {
				val data = sender.chat
				data.chatSpyExcludeChannels = data.chatSpyExcludeChannels.minus(channel)
				sender.sendMessage("$SYSPREFIX Removed '$channel' from your chat spy excluded channels list")
			}

			@Command(aliases = ["list"], desc = "List your chat spy excluded channels")
			@Require(Permission.Command.ChatSpy)
			fun list(@Sender sender: Player) {
				val data = sender.chat
				val channels = data.chatSpyExcludeChannels
				sender.sendMessage("$SYSPREFIX These channels are currently excluded from your chat spy: \n" +
						channels.map { it.toString() }.joinToString("\n    "))
			}

			@Command(aliases = ["clear"], desc = "List your chat spy excluded channels")
			@Require(Permission.Command.ChatSpy)
			fun clear(@Sender sender: Player) {
				val data = sender.chat
				data.chatSpyExcludeChannels = emptySet()
				sender.sendMessage("$SYSPREFIX Cleared your chat spy excluded channels list")
			}
		}
	}

	open class AbstractChatChannelCommand(private val plugin: Plugin, private val channel: ChatChannel, private val permission: String) : CommandExecutor {
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

			plugin.launch {
				try {
					if (message.isEmpty()) {
						if (sender !is Player) return@launch
						sender.chat.channel = channel
					} else if (channel is CommandSenderCompatibleChatChannel) {
						if (sender is Player) {
							sender.chat.doChat(channel, message)
						} else {
							channel.sendMessage(sender, message)
						}
					} else {
						(sender as Player).chat.doChat(channel, message)
					}
				} catch (e: ChatIllegalArgumentException) {
					sender.sendError(e.message ?: "Illegal argument.")
				} catch (e: ChatIllegalStateException) {
					sender.sendError(e.message ?: "Illegal state.")
				}
			}

			return true
		}
	}

	open class CommandChatParty(private val plugin: Plugin,
								private val profileManager: ProfileManager,
								private val characters: CharacterRepository,
								private val parties: PartyRepository,
								private val partySpectatorManager: PartySpectatorManager) : CommandExecutor {
		override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
			plugin.launch {
				if (!sender.hasPermission(Permission.Channel.Party)) {
					sender.sendError("Permission denied.")
					return@launch
				}

				val message = args.joinToString(" ")
				if (sender !is Player) {
					sender.sendError("You have to be a Player to use this command. You are a ${sender::class.java.simpleName}.")
					return@launch
				}

				val character = (sender as? Player)?.let { profileManager.getCurrentForPlayer(it) }?.let { characters.forProfile(it) }
				val party = character?.let { parties.forMember(it) } ?: partySpectatorManager.getParty(sender)

				if (party == null) {
					sender.sendError("You are not a member of any party.")
					return@launch
				}

				val channel = ChatParty(party)

				try {
					if (message.isEmpty()) {
						sender.chat.channel = channel
					} else {
						if (!FablesChatEvent(sender, channel, message, channel.getRecipients(sender).toSet()).callEvent()) {
							return@launch
						}

						sender.chat.doChat(channel, message)
					}
				} catch (e: ChatIllegalArgumentException) {
					sender.sendError(e.message ?: "Illegal argument.")
				} catch (e: ChatIllegalStateException) {
					sender.sendError(e.message ?: "Illegal state.")
				}
			}

			return true
		}
	}

	class CommandChatLocalOutOfCharacter(plugin: Plugin) : AbstractChatChannelCommand(plugin, ChatLocalOutOfCharacter, Permission.Channel.Looc)
	class CommandChatSpectator(plugin: Plugin) : AbstractChatChannelCommand(plugin, ChatSpectator, Permission.Channel.Spectator)
	class CommandChatStaff(plugin: Plugin) : AbstractChatChannelCommand(plugin, ChatStaff, Permission.Channel.Staff)
	class CommandChatOutOfCharacter(plugin: Plugin) : AbstractChatChannelCommand(plugin, ChatOutOfCharacter, Permission.Channel.Ooc)
	class CommandChatInCharacter(plugin: Plugin) : AbstractChatChannelCommand(plugin, ChatInCharacter, Permission.Channel.Ic)
	class CommandChatDirectMessage(private val plugin: Plugin) : CommandExecutor, TabCompleter {
		override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
				val playerName = args.getOrNull(0)
				if (playerName == null) {
					sender.sendError("You must specify a player name")
					return false
				}

				plugin.launch {
					try {
						val channel = ChatChannel.fromStringAliased("dm", sender)!!

						val content = args.slice(1 until args.size).joinToString(" ")
						val subChannel = (channel as SubChanneledChatChannel).resolveSubChannel("#$playerName").first
						if (content.isEmpty()) {
							if (sender is Player) {
								sender.chat.channel = subChannel
							}
							return@launch
						}

						if (sender is Player && !FablesChatEvent(sender, channel, content, subChannel.getRecipients(sender).toSet()).callEvent()) {
							return@launch
						}

						val message = "#$playerName $content"
						if (sender is Player) {
							channel.sendMessage(sender, message)
						} else if (channel is CommandSenderCompatibleChatChannel){
							channel.sendMessage(sender, message)
						} else {
							sender.sendError("You must be a Player to chat in this channel.")
							return@launch
						}
					} catch (e: ChatIllegalArgumentException) {
						sender.sendError(e.message ?: "Illegal argument.")
					} catch (e: ChatIllegalStateException) {
						sender.sendError(e.message ?: "Illegal state.")
					} catch (e: ChatUnsupportedOperationException) {
						sender.sendError(e.message ?: "Unsupported operation.")
					}
				}

			return true
		}

		override fun onTabComplete(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): MutableList<String>? {
			if (args.size > 1) return mutableListOf()

			return Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(args[0].lowercase()) }.toMutableList()
		}
	}

	class CommandReply(private val plugin: Plugin) : CommandExecutor {
		override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
			plugin.launch {
				val channel = ChatChannel.fromStringAliased("dm", sender)
				if (channel == null) {
					sender.sendError("Unknown channel")
					return@launch
				}

				val content = args.joinToString(" ")
				if (content.isEmpty()) {
					if (sender is Player) {
						sender.chat.channel = channel
					}
					return@launch
				}

				if (sender is Player && !FablesChatEvent(sender, channel, content, channel.getRecipients(sender).toSet()).callEvent()) {
					return@launch
				}

				try {
					if (sender is Player) {
						channel.sendMessage(sender, content)
					} else if (channel is CommandSenderCompatibleChatChannel){
						channel.sendMessage(sender, content)
					} else {
						sender.sendError("You must be a Player to chat in this channel.")
						return@launch
					}
				} catch (e: ChatIllegalArgumentException) {
					sender.sendError(e.message ?: "Illegal argument.")
				} catch (e: ChatIllegalStateException) {
					sender.sendError(e.message ?: "Illegal state.")
				} catch (e: ChatUnsupportedOperationException) {
					sender.sendError(e.message ?: "Unsupported operation.")
				}
			}

			return true
		}
	}
}
