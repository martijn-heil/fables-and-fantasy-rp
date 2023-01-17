package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.playerinstance.playerInstanceManager
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import com.fablesfantasyrp.plugin.utils.FABLES_ADMIN
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.ocpsoft.prettytime.PrettyTime

fun characterCard(character: Character): Component {
	val statsMessage = Component.text().append(CharacterStatKind.values().asSequence().map { statKind ->
		val statValue = character.stats[statKind]
		miniMessage.deserialize("  <gold>»</gold> <green><stat_name>:</green> <white><stat_value></white>",
				Placeholder.unparsed("stat_name", statKind.toString().replaceFirstChar { it.uppercaseChar() }),
				Placeholder.unparsed("stat_value", statValue.toString())
		)
	}.join(Component.newline()).asIterable()).build()

	return miniMessage.deserialize(
			"<newline>" +
					"<gray>═════ <white><player_name></white> <dark_gray>Character #<id></dark_gray> ═════</gray><newline>" +
					"<red>Please do not metagame this information.</red><newline>" +
					"<staff_character>" +
					"<newline>" +
					"<green>" +
					"Name: <white><name></white><newline>" +
					"Age: <white><age></white><newline>" +
					"Gender: <white><gender></white><newline>" +
					"Race: <white><race></white><newline>" +
					"Last seen: <white>${character.lastSeen?.let { PrettyTime().format(it) } ?: "unknown" }</white><newline>" +
					"Description: <white><description></white><newline>" +
					"Owned by: <white><owner></white><newline>" +
					"Maximum health: <white><maximum_health></white><newline>" +
					"Stats:<newline>" +
					"<stats><newline>" +
					"</green>",
			Placeholder.unparsed("player_name",
					playerInstanceManager.getCurrentForPlayerInstance(character.playerInstance)?.name ?: "(unknown player)"),
			Placeholder.component("staff_character",
					if (character.playerInstance.owner == FABLES_ADMIN)
						Component.text("This is a staff character").color(NamedTextColor.YELLOW)
					else Component.text()),
			Placeholder.unparsed("id", character.id.toString()),
			Placeholder.unparsed("owner", character.playerInstance.owner.name ?: character.playerInstance.owner.uniqueId.toString()),
			Placeholder.unparsed("name", character.name),
			Placeholder.unparsed("age", character.age.toString()),
			Placeholder.unparsed("gender", character.gender.toString().replaceFirstChar { it.uppercaseChar() }),
			Placeholder.unparsed("race", character.race.toString()),
			Placeholder.component("description", parseLinks(character.description)),
			Placeholder.unparsed("maximum_health", character.maximumHealth.toString()),
			Placeholder.component("stats", statsMessage),
	)
}
