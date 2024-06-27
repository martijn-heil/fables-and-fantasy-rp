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
package com.fablesfantasyrp.plugin.characters.command

import com.fablesfantasyrp.plugin.characters.Permission
import com.fablesfantasyrp.plugin.characters.SYSPREFIX
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender

class CharacterTraitCommand {
	inner class CharacterTraitCommand {
		@Command(aliases = ["assign"], desc = "Assign a trait to a character")
		@Require(Permission.Command.CharacterTrait.Assign)
		fun assign(@Sender sender: CommandSender, trait: CharacterTrait, @CommandTarget to: Character) {
			to.traits = to.traits.plus(trait)
			sender.sendMessage("$SYSPREFIX Assigned $trait to ${to.name}")
		}

		@Command(aliases = ["revoke"], desc = "Revoke trait from a character")
		@Require(Permission.Command.CharacterTrait.Revoke)
		fun revoke(@Sender sender: CommandSender, trait: CharacterTrait, @CommandTarget from: Character) {
			from.traits = from.traits.minus(trait)
			sender.sendMessage("$SYSPREFIX Revoked $trait from ${from.name}")
		}

		@Command(aliases = ["list"], desc = "List traits of a character")
		@Require(Permission.Command.CharacterTrait.List)
		fun list(@Sender sender: CommandSender, @CommandTarget from: Character) {
			sender.sendMessage(miniMessage.deserialize(
				"<prefix> <gray><character_name> has the following traits:<newline><traits></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("character_name", from.name),
				Placeholder.component("traits", Component.join(JoinConfiguration.newlines(),
					from.traits.map { Component.text(it.name) })))
			)
		}
	}
}
