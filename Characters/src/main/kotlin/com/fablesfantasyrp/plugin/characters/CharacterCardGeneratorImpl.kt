package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.utils.Services
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.milkbowl.vault.chat.Chat
import org.apache.commons.lang.WordUtils
import org.bukkit.command.CommandSender
import org.ocpsoft.prettytime.PrettyTime
import java.time.format.TextStyle

class CharacterCardGeneratorImpl(private val creatureSizeCalculator: CreatureSizeCalculator) : CharacterCardGenerator {
	override fun card(character: Character, observer: CommandSender?): Component {
		val profileManager = Services.get<ProfileManager>()
		val characterTraitRepository = Services.get<CharacterTraitRepository>()
		val vaultChat = Services.get<Chat>()
		val authorizer = Services.get<CharacterAuthorizer>()

		val statsMessage = Component.text().append(CharacterStatKind.values().asSequence().map { statKind ->
			val statValue = character.totalStats[statKind]
			miniMessage.deserialize("     <gold>»</gold> <green><stat_name>:</green> <white><stat_value></white>",
				Placeholder.unparsed("stat_name", statKind.toString().replaceFirstChar { it.uppercaseChar() }),
				Placeholder.unparsed("stat_value", statValue.toString())
			)
		}.join(Component.newline()).asIterable()).build()

		fun editButton(canEdit: Boolean, property: String)
			= if (canEdit)
			Component.text("[E]")
				.color(NamedTextColor.GOLD)
				.hoverEvent(HoverEvent.showText(Component.text("Click to change!")))
				.clickEvent(ClickEvent.runCommand("/char change $property #${character.id}"))
		else Component.text("[E]").color(NamedTextColor.DARK_GRAY)

		val player = profileManager.getCurrentForProfile(character.profile)
		val isMetaGamer = if (player != null) vaultChat.playerInGroup(player, "metagamer") else false

		val creatureSize = creatureSizeCalculator.getCreatureSize(character)

		return miniMessage.deserialize(
			"<newline>" +
				"<gray>═════ <white><player_name></white> <dark_gray>Character #<id></dark_gray> <status>═════</gray><newline>" +
				"<gray><owner_info>, last seen: <last_seen></gray><newline>" +
				"<red>Please do not metagame this information.</red><newline>" +
				"<metagamer_warning>" +
				"<green>" +
				"<change_name> Name: <white><name></white><newline>" +
				"<change_dateofbirth> Age: <white><age></white><newline>" +
				"<change_gender> Gender: <white><gender></white><newline>" +
				"<change_race> Race: <white><race></white><newline>" +
				"<black>[E]</black> Creature size: <white><creature_size></white><newline>" +
				"<black>[E]</black> Character traits: <white><traits></white><newline>" +
				"<change_description> Description: <white><description></white><newline>" +
				"<black>[E]</black> Maximum health: <white><maximum_health></white><newline>" +
				"<change_stats> Stats:<newline>" +
				"<stats><newline>" +
				"</green>",
			Placeholder.unparsed("player_name", player?.name ?: "(offline)"),
			Placeholder.unparsed("id", character.id.toString()),
			Placeholder.component("owner_info",
				if (character.isStaffCharacter) Component.text("This is a staff character").color(NamedTextColor.YELLOW)
				else if (character.profile.owner != null) miniMessage.deserialize("Owned by <name>",
					Placeholder.unparsed("name", character.profile.owner!!.name ?: character.profile.owner!!.uniqueId.toString()))
				else Component.text("This character is not owned by anyone")
			),
			Placeholder.component("metagamer_warning",
				if (isMetaGamer) miniMessage.deserialize("<dark_red>WARNING: This user is a registered metagamer.</dark_red><newline>")
				else Component.empty()
			),
			Placeholder.component("name", Component.text(character.name)),
			Placeholder.component("age", character.age?.let { age ->
				Component.text(age.toString()).hoverEvent(
				HoverEvent.showText(Component.text("Date of birth: " + formatDateFancy(character.dateOfBirth!!))
					.color(NamedTextColor.GRAY)
					.decoration(TextDecoration.ITALIC, false)))
			} ?: Component.text("Unknown")),
			Placeholder.unparsed("gender", character.gender.toString().replaceFirstChar { it.uppercaseChar() }),
			Placeholder.unparsed("race", character.race.toString()),
			Placeholder.unparsed("creature_size", creatureSize.displayName),
			Placeholder.component("description", parseLinks(character.description)),
			Placeholder.unparsed("maximum_health", character.maximumHealth.toString()),
			Placeholder.component("traits", formatTraits(characterTraitRepository.forCharacter(character))),
			Placeholder.component("stats", statsMessage),
			Placeholder.unparsed("last_seen", character.lastSeen?.let { PrettyTime().format(it) } ?: "unknown"),
			Placeholder.component("change_name", editButton(observer == null || authorizer.mayEditName(observer, character).result, "name")),
			Placeholder.component("change_dateofbirth", editButton(observer == null || authorizer.mayEditDateOfBirth(observer, character).result, "dateofbirth")),
			Placeholder.component("change_description", editButton(observer == null || authorizer.mayEditDescription(observer, character).result, "description")),
			Placeholder.component("change_race", editButton(observer == null || authorizer.mayEditRace(observer, character).result, "race")),
			Placeholder.component("change_gender", editButton(observer == null || authorizer.mayEditGender(observer, character).result, "gender")),
			Placeholder.component("change_stats", editButton(observer == null || authorizer.mayEditStats(observer, character).result, "stats")),
			Placeholder.component("status", if (character.isDead) Component.text("(dead) ").color(NamedTextColor.RED)
			else if (character.isShelved) Component.text("(shelved) ").color(NamedTextColor.YELLOW)
			else Component.empty())
		)
	}

	private fun formatTraits(traits: Collection<CharacterTrait>): Component {
		return Component.join(JoinConfiguration.separator(Component.text(", ")),
			traits.map {
				val descriptionLines = (it.description ?: "No description.")
					.let { WordUtils.wrap(it, 40) }
					.lines()
					.map { Component.text(it) }
					.let { Component.join(JoinConfiguration.newlines(), it) }

				val description = miniMessage.deserialize(
					"<bold><display_name></bold><newline>" +
					"<gray><lines></gray>",
					Placeholder.unparsed("display_name", it.displayName),
					Placeholder.component("lines", descriptionLines)
				)

				Component.text(it.displayName).hoverEvent(HoverEvent.showText(description))
			}
		)
	}

	private fun formatDateFancy(date: FablesLocalDate): String {
		val dayOfWeekName = date.dayOfWeek.getDisplayName(TextStyle.FULL)
		val monthName = date.getMonth().getDisplayName(TextStyle.FULL)

		return "$dayOfWeekName ${date.dayOfMonth}, $monthName, ${date.year}"
	}
}
