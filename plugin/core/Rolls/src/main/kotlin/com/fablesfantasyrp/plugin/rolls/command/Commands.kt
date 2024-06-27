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
package com.fablesfantasyrp.plugin.rolls.command

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.parametric.annotation.Optional
import com.fablesfantasyrp.caturix.spigot.common.FixedSuggestions
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.spigot.common.Suggestions
import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.rolls.ROLL_RANGE
import com.fablesfantasyrp.plugin.rolls.flaunch
import com.fablesfantasyrp.plugin.rolls.rollExpression
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.broadcast
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.plugin.Plugin

class Commands(private val plugin: Plugin,
			   private val characters: CharacterRepository,
			   private val profileManager: ProfileManager) {
	private val server = plugin.server

	@Command(aliases = ["roll", "dice"], desc = "Roll the dice!")
	@Require("fables.rolls.command.roll")
	fun roll(@Sender sender: Profile,
			 @FixedSuggestions @Suggestions(["3", "6", "20", "100"]) @Optional("20") expression: String,
			 @Optional kind: CharacterStatKind?) {
		val senderPlayer = profileManager.getCurrentForProfile(sender) ?: throw IllegalStateException()
		flaunch {
			val senderCharacter = characters.forProfile(sender)
			val senderName = senderCharacter?.name ?: senderPlayer.name
			val prefix = legacyText(GLOBAL_SYSPREFIX)
			val chatStyle = senderPlayer.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW)
			val resolver = TagResolver.builder().tag("chat_color", Tag.styling { it.merge(chatStyle) }).build()

			val isComplex = !expression.matches(Regex("[0-9]+"))

			if (isComplex) {
				val result = try {
					rollExpression(expression, senderCharacter, kind)
				} catch (ex: Exception) {
					senderPlayer.sendError(ex.message ?: "Unknown error")
					return@flaunch
				}

				server.broadcast(senderPlayer.location, 15,
					miniMessage.deserialize(
						"<prefix> <yellow><name></yellow> <chat_color>rolls <kind><expression> = <bold><result></bold></chat_color>",
						Placeholder.component("prefix", prefix),
						Placeholder.unparsed("expression", expression),
						Placeholder.unparsed("kind", if (kind != null) "${kind.shortName} " else ""),
						Placeholder.unparsed("result", result.toString()),
						Placeholder.unparsed("name", senderName),
						resolver
					)
				)
				return@flaunch
			} else {
				if (senderCharacter == null && kind != null) {
					senderPlayer.sendError("You cannot roll a specific stat kind while you are out of character")
					return@flaunch
				}

				val dice = expression.toIntOrNull() ?: run {
					senderPlayer.sendError("Could not parse '$expression' as an integer")
					return@flaunch
				}

				val roll = com.fablesfantasyrp.plugin.rolls.roll(dice.toUInt(), senderCharacter, kind)
				val result = roll.second

				val letter = when(kind) {
					CharacterStatKind.AGILITY -> "a"
					CharacterStatKind.DEFENSE -> "d"
					CharacterStatKind.STRENGTH -> "s"
					CharacterStatKind.INTELLIGENCE -> "i"
					null -> ""
				}

				val message = miniMessage.deserialize(
					"<prefix> <yellow><name></yellow> <chat_color>rolls d<dice><stat_letter> = <bold><result></bold></chat_color>",
					Placeholder.component("prefix", prefix),
					Placeholder.unparsed("dice", dice.toString()),
					Placeholder.unparsed("result", result.toString()),
					Placeholder.unparsed("stat_letter", letter),
					Placeholder.unparsed("name", senderName),
					resolver
				)

				server.broadcast(senderPlayer.location, ROLL_RANGE.toInt(), message)
			}
		}
	}
}
