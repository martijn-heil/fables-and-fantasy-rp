package com.fablesfantasyrp.plugin.morelogging

import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.denizeninterop.denizenRun
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.apache.logging.log4j.Level
import org.bukkit.Bukkit
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Level as JulLevel

/**
 * All log events get logged to file.
 * Log events with level equal to or greater than FINER can be received by online moderators.
 * Log events with level equal to or greater than FINE get logged to console.
 * Log events with level equal to or greater than INFO get sent to all online moderators.
 * Log events with level equal to or greater than WARN get sent to all online moderators and logged to discord.
 * Log events with level equal to or greater than SEVERE get sent to discord with a ping to the mod team.
 *
 * Flight modes -> FINEST
 * All chat and commands -> FINER
 * Significant state switches -> FINE
 * Knockouts -> INFO
 * Lockpicking success, executions etc -> WARN
 */
class ModerationLogHandler : Handler() {
	override fun publish(record: LogRecord?) {
		if (record == null) return

		val level = record.level
		val log4jLevel = when (level) {
			java.util.logging.Level.ALL -> Level.ALL
			java.util.logging.Level.INFO -> Level.INFO
			java.util.logging.Level.WARNING -> Level.WARN
			java.util.logging.Level.SEVERE -> Level.FATAL
			java.util.logging.Level.CONFIG -> Level.INFO
			java.util.logging.Level.FINE -> Level.DEBUG
			java.util.logging.Level.FINER -> Level.TRACE
			java.util.logging.Level.FINEST -> Level.TRACE
			java.util.logging.Level.OFF -> Level.OFF
			else -> Level.INFO
		}

		LOG4J_MODERATION_LOGGER.log(log4jLevel, record.message)

		if (level.intValue() >= JulLevel.FINE.intValue()) {
			val newLevel = if (record.level.intValue() < JulLevel.INFO.intValue()) JulLevel.INFO else level
			Bukkit.getLogger().log(newLevel, record.message)
		}

		if (level.intValue() >= JulLevel.SEVERE.intValue()) {
			this.logToDiscord(record.message, true)
		} else if (level.intValue() >= JulLevel.WARNING.intValue()) {
			Bukkit.broadcast(Component.text(record.message).color(NamedTextColor.GREEN), Permission.Log.Receive)
			Bukkit.getOnlinePlayers().asSequence()
					.filter { it.hasPermission(Permission.Log.Receive) }
					.forEach { it.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE,
							"block.anvil.land"), Sound.Source.MASTER,
							1.0f, 1.0f)) }
			this.logToDiscord(record.message)
		} else if (level.intValue() >= JulLevel.INFO.intValue()) {
			Bukkit.broadcast(Component.text(record.message).color(NamedTextColor.GRAY), Permission.Log.Receive)
		}
	}

	override fun flush() {
		// do nothing
	}

	override fun close() {
		// do nothing
	}

	private fun logToDiscord(message: String, ping: Boolean = false) {
		// Magic values @mention Junior Moderator and Moderator
		// Can be obtained with
		// ex narrate "<proc[discord_group].context[[F&F] - Moderation Team].role[Junior Moderator].mention>"

		val finalMessage = if (ping) "`$message` \n<@&922294150810972161> <@&852176608780877886>" else "`$message`"

		denizenRun("discord_say", mapOf(
				Pair("groupname", ElementTag("[F&F] - Moderation Team")),
				Pair("channelname", ElementTag("logging")),
				Pair("message", ElementTag(finalMessage)),
		))
	}
}
