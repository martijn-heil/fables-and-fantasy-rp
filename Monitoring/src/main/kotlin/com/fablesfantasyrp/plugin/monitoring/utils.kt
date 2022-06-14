package com.fablesfantasyrp.plugin.monitoring

import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.denizeninterop.denizenRun


fun logToDiscord(message: String) {
	denizenRun("discord_say", mapOf(
			Pair("groupname", ElementTag("Fables and Fantasy RP")),
			Pair("channelname", ElementTag("tech-monitoring")),
			Pair("message", ElementTag(message)),
	))
}
