package com.fablesfantasyrp.plugin.activity.command

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.plugin.activity.Permission
import com.fablesfantasyrp.plugin.activity.domain.repository.ActivityRegionRepository
import com.fablesfantasyrp.plugin.text.miniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender

class Commands(private val activityRegions: ActivityRegionRepository) {
	@Command(aliases = ["activity"], desc = "Show player activity")
	@Require(Permission.Command.Activity)
	suspend fun activity(@Sender sender: CommandSender) {
		val regions = activityRegions.all().asSequence()
			.map { Pair(it, it.countPlayers()) }
			.filter { it.second > 0 }
			.sortedByDescending { it.second }
			.map { miniMessage.deserialize("<green><player_count></green> <gray>in <display_name></gray>",
				Placeholder.unparsed("player_count", it.second.toString()),
				Placeholder.unparsed("display_name", it.first.name)) }
			.toList()

		val message = miniMessage.deserialize(
			"<dark_gray>===== <gold><bold>Current Player Activity</bold></gold> =====</dark_gray><newline>" +
			"<red>Please do not use this information with malicious intent.</red><newline>" +
			"<regions>",
			Placeholder.component("regions", Component.join(JoinConfiguration.newlines(), regions)))

		sender.sendMessage(message)
	}
}
