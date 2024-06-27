/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
		flaunch { main() }
	}

	private suspend fun main() {
		kord = Kord(token)

		nationDiscords = config.nationDiscords.mapNotNull { kord.getGuildOrNull(it) }.toSet()

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			flaunch {
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
				flaunch {
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
