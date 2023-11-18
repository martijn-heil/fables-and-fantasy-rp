package com.fablesfantasyrp.plugin.discord

import com.fablesfantasyrp.plugin.text.sendError
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.time.Instant

data class DiscordCalendarEvent(val title: String, val start: Long, val end: Long)
fun GuildScheduledEvent.transform() = DiscordCalendarEvent(
	title = this.name,
	start = this.scheduledStartTime.epochSeconds,
	end = this.scheduledEndTime?.epochSeconds ?: Instant.MAX.epochSecond
)

data class FablesDiscordBotConfig(
	val token: String,
	val nationDiscords: Set<Snowflake>)

class FablesDiscordBot(private val plugin: JavaPlugin, private val config: FablesDiscordBotConfig) {
	lateinit var kord: Kord
		private set
	private val logger = plugin.logger
	private val server = plugin.server

	private val token = config.token

	private val COMMAND_PREFIX = "?"
	private val IGNORE_COMMANDS = setOf("character", "parse")

	suspend fun getMainDiscord() = kord.getGuildOrThrow(MAIN_DISCORD_ID)
	suspend fun getSupportDiscord() = kord.getGuildOrThrow(SUPPORT_DISCORD_ID)
	lateinit var nationDiscords: Set<Guild>
		private set

	fun getCalendarEvents(): Set<DiscordCalendarEvent> = synchronized(calendarEventsLock) { return calendarEvents }
	private var calendarEventsLock = Unit
	private var calendarEvents: Set<DiscordCalendarEvent> = emptySet()

	fun start() {
		plugin.launch { main() }
	}

	private suspend fun main() {
		kord = Kord(token)

		nationDiscords = config.nationDiscords.mapNotNull { kord.getGuildOrNull(it) }.toSet()

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			plugin.launch {
				async {
					val events = nationDiscords.map { it.scheduledEvents }.merge()
					val results = events.map { it.transform() }.toSet()
					synchronized(calendarEventsLock) { calendarEvents = results }
				}
			}
		}, 0, 20*60*5)

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

			if (command.isEmpty() || IGNORE_COMMANDS.contains(command.split(" ")[0])) return@on

			val channel = message.channel
			val sender = DiscordCommandSender.build(this.member!!.asUser()) {
				plugin.launch {
					channel.createMessage(it)
				}
			}

			val mainDiscordMember = message.author?.asMemberOrNull(MAIN_DISCORD_ID)

			if (mainDiscordMember?.roleIds?.contains(WANDERER_ROLE_ID) != true) {
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
