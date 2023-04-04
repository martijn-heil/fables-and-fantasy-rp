package com.fablesfantasyrp.plugin.playeresp.command

import com.fablesfantasyrp.plugin.playeresp.Permission
import com.fablesfantasyrp.plugin.playeresp.PlayerEspManager
import com.fablesfantasyrp.plugin.playeresp.SYSPREFIX
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import com.fablesfantasyrp.plugin.utils.asEnabledDisabledComponent
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val espManager: PlayerEspManager) {
	@Command(aliases = ["playeresp", "esp"], desc = "Toggle PlayerESP")
	@Require(Permission.Command.Playeresp)
	fun playeresp(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Playeresp + ".others") target: Player) {
		val hasEsp = espManager.hasEsp(target)
		espManager.setEsp(target, !hasEsp)
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> <status> PlayerESP for <name>.</gray>",
			Placeholder.component("prefix", legacyText(SYSPREFIX)),
			Placeholder.component("status", (!hasEsp).asEnabledDisabledComponent()),
			Placeholder.component("name", Component.text(target.name).style(target.nameStyle))
		))
	}
}
