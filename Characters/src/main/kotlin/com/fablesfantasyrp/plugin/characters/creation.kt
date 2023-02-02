package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.gui.CharacterStatsGui
import com.fablesfantasyrp.plugin.form.promptChat
import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.Services
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.single
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import java.time.Instant
import kotlin.coroutines.cancellation.CancellationException

data class NewCharacterData(val name: String, val age: UInt, val gender: Gender, val race: Race,
							val description: String, val stats: CharacterStats)

fun getAllowedRaces(isStaffCharacter: Boolean): Collection<Race> = Race.values().asSequence()
				.filter { it != Race.HUMAN }
				.filter { if (!isStaffCharacter) it != Race.OTHER else true }
				.toList()

val NAME_DISALLOWED_CHARACTERS = Regex("[^A-Za-z0-9\\- ']")

val Race.itemStackRepresentation: ItemStack get() = ItemStack(when (this) {
			Race.HUMAN -> Material.HAY_BLOCK
			Race.ATTIAN_HUMAN -> Material.HAY_BLOCK
			Race.KHADAN_HUMAN -> Material.HAY_BLOCK
			Race.HINTERLANDER_HUMAN -> Material.HAY_BLOCK
			Race.HIGH_ELF -> Material.PURPLE_TERRACOTTA
			Race.DARK_ELF -> Material.COPPER_BLOCK
			Race.WOOD_ELF -> Material.OAK_LOG
			Race.DWARF -> Material.DEEPSLATE_BRICKS
			Race.TIEFLING -> Material.MAGMA_BLOCK
			Race.ORC -> Material.MOSS_BLOCK
			Race.GOBLIN -> Material.LIGHT_GRAY_TERRACOTTA
			Race.HALFLING -> Material.CRAFTING_TABLE
			Race.OTHER -> Material.CARVED_PUMPKIN
		})

suspend fun promptNewCharacterInfo(player: Player, allowedRaces: Collection<Race>): NewCharacterData {
	player.sendMessage(miniMessage.deserialize("<gray>Welcome, <yellow><player_name></yellow>!</gray>",
			Placeholder.unparsed("player_name", player.name)))

	player.sendMessage(miniMessage.deserialize("<gray>Let's create your brand new character!</gray>"))

	var name: String
	while (true) {
		name = player.promptChat(miniMessage.deserialize("<gray>What is the name of your character?</gray> " +
				"<dark_gray>(max 32 characters)</dark_gray>"))
				.replace("#", "")
				.replace("&", "")

		if (name.length > 32) {
			player.sendError("Your character name must not be longer than 32 characters.")
			continue
		}

		if (NAME_DISALLOWED_CHARACTERS.containsMatchIn(name)) {
			player.sendError("Your character name contains illegal characters, please only use alphanumeric characters and single quotes.")
			continue
		}

		if (name.isBlank()) {
			player.sendError("Your character name is blank")
			continue
		}

		break
	}

	val age: UInt = flow {
		val message = miniMessage.deserialize("<gray>What is the age of <yellow><character_name></yellow>?</gray> " +
				"<dark_gray>(between 13 and 1000)</dark_gray>",
				Placeholder.unparsed("character_name", name))

		val num = player.promptChat(message).toUIntOrNull()
		if (num == null) throw IllegalArgumentException("Could not parse integer")
		if (num < 13U) throw IllegalArgumentException("Age must be at least 13")
		if (num > 1000U) throw IllegalArgumentException("Age may not be greater than 1000")
		emit(num)
	}.retry {
		if (it is IllegalArgumentException) {
			player.sendError(it.message ?: "unknown"); true
		} else false
	}.single()

	val gender = player.promptGui(GuiSingleChoice<Gender>(FablesCharacters.instance,
			"Please choose a gender",
			Gender.values().asSequence(),
			{
				ItemStack(when (it) {
					Gender.MALE -> Material.CYAN_WOOL
					Gender.FEMALE -> Material.PINK_WOOL
					Gender.OTHER -> Material.WHITE_WOOL
				})
			},
			{ "${ChatColor.GOLD}" + it.toString().replaceFirstChar { firstChar -> firstChar.uppercase() } }
	))
	player.sendMessage(miniMessage.deserialize("<gray>Your character's gender is <yellow><gender></yellow>!</gray>",
			Placeholder.unparsed("gender", gender.toString())))

	val race = player.promptGui(GuiSingleChoice<Race>(FablesCharacters.instance,
			"Please choose a race",
			allowedRaces.asSequence(),
			{ it.itemStackRepresentation },
			{ "${ChatColor.GOLD}$it" }
	))

	val description = player.promptChat(miniMessage.deserialize("<gray>What is the description of <yellow><name></yellow>?</gray>",
			Placeholder.unparsed("name", name)))

	player.sendMessage(miniMessage.deserialize("<gray>Your character's description is <yellow><description></yellow></gray>",
			Placeholder.unparsed("description", description)))

	val baseStats = CharacterStats(2U, 2U, 2U, 2U) + race.boosters
	val stats = player.promptGui(CharacterStatsGui(FablesCharacters.instance, baseStats, "Please choose $name's stats"))
	Component.text().color(NamedTextColor.GRAY)
			.append(Component.text("Your character's stats are "))
			.append(
					CharacterStatKind.values().asSequence().map {
						val displayName = it.toString().replaceFirstChar { firstChar -> firstChar.uppercaseChar() }
						val statValue = stats[it]
						Component.text("$statValue $displayName").color(NamedTextColor.YELLOW)
					}.join(Component.text(", ")).asIterable()
			)

	player.sendMessage(miniMessage.deserialize(
			"<gray>" +
					"Your character's stats are " +
					"<yellow><strength> Strength</yellow>, " +
					"<yellow><defense> Defense</yellow>, " +
					"<yellow><agility> Agility</yellow>, " +
					"<yellow><intelligence> Intelligence</yellow>, " +
					"</gray>",
			Placeholder.unparsed("strength", stats.strength.toString()),
			Placeholder.unparsed("defense", stats.defense.toString()),
			Placeholder.unparsed("agility", stats.agility.toString()),
			Placeholder.unparsed("intelligence", stats.intelligence.toString())))

	return NewCharacterData(name, age, gender, race, description, stats)
}

private val blockMovement = HashSet<Player>()
suspend fun forcePromptNewCharacterInfo(player: Player, allowedRaces: Collection<Race>): NewCharacterData {
	blockMovement.add(player)
	try {
		while (true) {
			if (!player.isOnline) throw CancellationException()
			try {
				val info = promptNewCharacterInfo(player, allowedRaces)

				if (characterRepository.nameExists(info.name)) {
					player.sendError("The name '${info.name}' is already in use, please enter a different name.")
					continue
				}

				return info
			} catch (_: CancellationException) {}
		}
	} finally {
		blockMovement.remove(player)
	}
}

suspend fun forceCharacterCreation(player: Player,
								   profiles: EntityProfileRepository = Services.get(),
								   characters: EntityCharacterRepository = Services.get(),
								   profileManager: ProfileManager = Services.get()) {
	val newCharacterData = forcePromptNewCharacterInfo(player, Race.values().filter { it != Race.HUMAN && it != Race.OTHER})

	val profile = profiles.create(Profile(
			owner = player,
			description = newCharacterData.name,
			isActive = true
	))

	val character = characters.create(Character(
			id = profile.id,
			profile = profile,
			name = newCharacterData.name,
			race = newCharacterData.race,
			gender = newCharacterData.gender,
			stats = newCharacterData.stats,
			age = newCharacterData.age,
			description = newCharacterData.description,
			lastSeen = Instant.now(),
			createdAt = Instant.now()
	))

	profileManager.setCurrentForPlayer(player, profile)
}

class CharacterCreationListener : Listener {
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerMove(e: PlayerMoveEvent) {
		if (blockMovement.contains(e.player)) e.isCancelled = true
	}
}