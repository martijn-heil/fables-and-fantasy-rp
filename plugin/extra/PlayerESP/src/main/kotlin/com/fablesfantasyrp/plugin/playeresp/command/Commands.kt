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
package com.fablesfantasyrp.plugin.playeresp.command

import com.fablesfantasyrp.plugin.playeresp.Permission
import com.fablesfantasyrp.plugin.playeresp.PlayerEspManager
import com.fablesfantasyrp.plugin.playeresp.SYSPREFIX
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import com.fablesfantasyrp.plugin.utils.asEnabledDisabledComponent
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
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
