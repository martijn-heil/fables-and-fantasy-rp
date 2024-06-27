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
package com.fablesfantasyrp.plugin.chat.channel

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.domain.DISTANCE_TALK
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.koin.core.context.GlobalContext
import java.io.Serializable

object ChatLocalOutOfCharacter : ChatChannel, RawChatChannel, ToggleableChatChannel, PreviewableChatChannel, Serializable {
	private val profileManager: ProfileManager = GlobalContext.get().get()
	private val characters: CharacterRepository = GlobalContext.get().get()

	override fun getRecipients(from: Player): Sequence<Player> =
			getPlayersWithinRange(from.location, DISTANCE_TALK)
					.filter { !it.chat.disabledChannels.contains(this) }

	override suspend fun sendMessage(from: Player, message: String) {
		this.sendMessage(from, parseLinks(message))
	}

	override suspend fun sendMessage(from: Player, message: Component) {
		if(PlainTextComponentSerializer.plainText().serialize(message).isEmpty()) return

		val final = this.formatMessage(from, message)
		val recipients = getRecipients(from).toList()
		recipients.forEach { it.sendMessage(final) }
		val loc = from.location
		logChatToConsole(Component.text()
				.append(Component.text("[${loc.humanReadable()}] "))
				.append(Component.text("${recipients.map { it.name }.sorted()} "))
				.append(final)
				.build())
	}

	private suspend fun formatMessage(from: Player, message: Component): Component {
		val profile = profileManager.getCurrentForPlayer(from)
		val character = profile?.let { characters.forProfile(it) }
		val characterName = character?.name ?: ""

		val customResolver = TagResolver.builder()
				.tag("character_name", Tag.selfClosingInserting(Component.text(characterName)))
				.tag("player_name", Tag.selfClosingInserting(Component.text(from.name)))
				.tag("message", Tag.selfClosingInserting(message))
				.build()
		return miniMessage.deserialize(
				"<click:run_command:/looc><yellow>[L]</yellow></click> <white><player_name></white> " +
						"<dark_gray>(</dark_gray><gray><character_name></gray><dark_gray>)</dark_gray> <yellow>»</yellow> " +
						"<gray><message></gray>", TagResolver.standard(), customResolver)
	}

	override suspend fun getPreview(from: Player, message: String): Component = this.formatMessage(from, parseLinks(message))
	override fun toString() = "looc"
	fun readResolve(): Any? = ChatLocalOutOfCharacter
	private const val serialVersionUID: Long = 1
}
