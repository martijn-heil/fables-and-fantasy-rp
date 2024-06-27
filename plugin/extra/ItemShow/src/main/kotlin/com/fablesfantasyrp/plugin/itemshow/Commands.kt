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
package com.fablesfantasyrp.plugin.itemshow

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.domain.DISTANCE_TALK
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.broadcast
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Commands(private val plugin: Plugin,
			   private val characters: CharacterRepository,
			   private val profileManager: ProfileManager) {
	private val server = plugin.server

	@Command(aliases = ["itemshow", "showitem"], desc = "")
	@Require("fables.itemshow.command.itemshow")
	fun itemshow(@Sender sender: Player) {
		flaunch {
			val item = sender.inventory.itemInMainHand
			if (item.type == Material.AIR) {
				sender.sendError("You must hold the item you want to show in your main hand.")
				return@flaunch
			}

			val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
			val displayName = character?.name ?: sender.name

			val itemDisplay = Component.text("[ HOVER OVER ME ]").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
				.hoverEvent(item.asHoverEvent())

			server.broadcast(sender.location, DISTANCE_TALK.toInt(),
				miniMessage.deserialize("<gray><prefix> <display_name> is displaying the item in their hand.<newline>" +
					"<item></gray>",
					Placeholder.component("prefix", legacyText(SYSPREFIX)),
					Placeholder.unparsed("display_name", displayName),
					Placeholder.component("item", itemDisplay)
				)
			)
		}
	}
}
