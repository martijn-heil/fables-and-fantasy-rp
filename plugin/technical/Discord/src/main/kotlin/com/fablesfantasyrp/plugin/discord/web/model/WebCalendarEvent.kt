package com.fablesfantasyrp.plugin.discord.web.model

import com.fablesfantasyrp.plugin.discord.DiscordCalendarEvent
import kotlinx.serialization.Serializable

@Serializable
data class WebCalendarEvent(val title: String, val start: Long, val end: Long)
fun DiscordCalendarEvent.transform() = WebCalendarEvent(
	title = this.title,
	start = this.start,
	end = this.end
)
