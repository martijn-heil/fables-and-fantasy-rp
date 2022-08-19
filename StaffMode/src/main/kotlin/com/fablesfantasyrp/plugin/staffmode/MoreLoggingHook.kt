package com.fablesfantasyrp.plugin.staffmode

import com.fablesfantasyrp.plugin.morelogging.MODERATION_LOGGER
import com.fablesfantasyrp.plugin.morelogging.logPlayerStateChange
import com.fablesfantasyrp.plugin.utils.ToggleableState
import org.bukkit.entity.Player
import java.util.logging.Level

class MoreLoggingHook {
	fun logDutySwitch(p: Player, newState: ToggleableState) {
		logPlayerStateChange(MODERATION_LOGGER, Level.FINE, p, "DUTY", newState.not().name, newState.name)
	}
}
