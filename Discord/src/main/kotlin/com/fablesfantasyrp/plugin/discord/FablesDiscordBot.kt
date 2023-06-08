package com.fablesfantasyrp.plugin.discord

import com.fablesfantasyrp.plugin.text.sendError
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin

class FablesDiscordBot(private val plugin: JavaPlugin, private val token: String) {
	lateinit var kord: Kord
		private set
	private val logger = plugin.logger
	private val server = plugin.server

	private val COMMAND_PREFIX = "?"
	private val IGNORE_COMMANDS = setOf("date", "character", "whitelist", "parse")

	suspend fun getMainDiscord() = kord.getGuildOrThrow(MAIN_DISCORD_ID)
	suspend fun getSupportDiscord() = kord.getGuildOrThrow(SUPPORT_DISCORD_ID)

	fun start() {
		plugin.launch { main() }
	}

	private suspend fun main() {
		kord = Kord(token)

		kord.on<MessageCreateEvent> { // runs every time a message is created that our bot can read
			// ignore other bots, even ourselves. We only serve humans here!
			if (message.author?.isBot != false) return@on

			// check if our command is being invoked
			if (message.content == "${COMMAND_PREFIX}ping") {
				message.channel.createMessage("pang!")
				return@on
			}

			if (!message.content.startsWith(COMMAND_PREFIX)) return@on
			val command = message.content.removePrefix(COMMAND_PREFIX)

			if (command.isEmpty() || IGNORE_COMMANDS.contains(command)) return@on

			val channel = message.channel
			val sender = DiscordCommandSender(this.member!!.asUser()) {
				plugin.launch {
					channel.createMessage(it)
				}
			}

			if (message.getAuthorAsMemberOrNull()?.roleIds?.contains(WANDERER_ROLE_ID) != true) {
				sender.sendError("You must be a Wanderer to execute commands.")
				return@on
			}

			withContext(plugin.minecraftDispatcher) {
				server.dispatchCommand(sender, command)
			}
		}

		// Suspends until logout() or shutdown() is called
		kord.login {
			this.intents = Intents(Intent.values)
		}
	}
}
