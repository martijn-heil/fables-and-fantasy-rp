package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.profile.profileManager
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.ocpsoft.prettytime.PrettyTime

fun characterCard(character: Character, observer: CommandSender? = null): Component {
	val statsMessage = Component.text().append(CharacterStatKind.values().asSequence().map { statKind ->
		val statValue = character.totalStats[statKind]
		miniMessage.deserialize("     <gold>»</gold> <green><stat_name>:</green> <white><stat_value></white>",
				Placeholder.unparsed("stat_name", statKind.toString().replaceFirstChar { it.uppercaseChar() }),
				Placeholder.unparsed("stat_value", statValue.toString())
		)
	}.join(Component.newline()).asIterable()).build()

	fun editButton(permission: String, property: String)
		= if (observer == null || observer.hasPermission(permission)) Component.text("[E]")
			.color(NamedTextColor.GOLD)
			.hoverEvent(HoverEvent.showText(Component.text("Click to change!")))
			.clickEvent(ClickEvent.runCommand("/char change $property #${character.id}"))
		else Component.text("[E]").color(NamedTextColor.DARK_GRAY)

	return miniMessage.deserialize(
			"<newline>" +
					"<gray>═════ <white><player_name></white> <dark_gray>Character #<id></dark_gray> <status>═════</gray><newline>" +
					"<gray><owner_info>, last seen: <last_seen></gray><newline>" +
					"<red>Please do not metagame this information.</red><newline>" +
					"<green>" +
					"<change_name> Name: <white><name></white><newline>" +
					"<change_age> Age: <white><age></white><newline>" +
					"<change_gender> Gender: <white><gender></white><newline>" +
					"<change_race> Race: <white><race></white><newline>" +
					"<change_description> Description: <white><description></white><newline>" +
					"<black>[E]</black> Maximum health: <white><maximum_health></white><newline>" +
					"<change_stats> Stats:<newline>" +
					"<stats><newline>" +
					"</green>",
			Placeholder.unparsed("player_name", profileManager.getCurrentForProfile(character.profile)?.name ?: "(offline)"),
			Placeholder.unparsed("id", character.id.toString()),
			Placeholder.component("owner_info",
					if (character.isStaffCharacter) Component.text("This is a staff character").color(NamedTextColor.YELLOW)
					else miniMessage.deserialize("Owned by <name>",
							Placeholder.unparsed("name",character.profile.owner.name ?: character.profile.owner.uniqueId.toString()))
			),
			Placeholder.component("name", Component.text(character.name)),
			Placeholder.component("age", Component.text(character.age.toString())),
			Placeholder.unparsed("gender", character.gender.toString().replaceFirstChar { it.uppercaseChar() }),
			Placeholder.unparsed("race", character.race.toString()),
			Placeholder.component("description", parseLinks(character.description)),
			Placeholder.unparsed("maximum_health", character.maximumHealth.toString()),
			Placeholder.component("stats", statsMessage),
			Placeholder.unparsed("last_seen", character.lastSeen?.let { PrettyTime().format(it) } ?: "unknown"),
			Placeholder.component("change_name", editButton(Permission.Command.Characters.Change.Name, "name")),
			Placeholder.component("change_age", editButton(Permission.Command.Characters.Change.Age, "age")),
			Placeholder.component("change_description", editButton(Permission.Command.Characters.Change.Description, "description")),
			Placeholder.component("change_race", editButton(Permission.Command.Characters.Change.Race, "race")),
			Placeholder.component("change_gender", editButton(Permission.Command.Characters.Change.Gender, "gender")),
			Placeholder.component("change_stats", editButton(Permission.Command.Characters.Change.Stats, "stats")),
			Placeholder.component("status", if (character.isDead) Component.text("(dead) ").color(NamedTextColor.RED)
											else if (character.isShelved) Component.text("(shelved) ").color(NamedTextColor.YELLOW)
											else Component.empty())
	)
}