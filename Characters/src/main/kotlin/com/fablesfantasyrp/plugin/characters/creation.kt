package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.gui.CharacterStatsGui
import com.fablesfantasyrp.plugin.form.promptChat
import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.single
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.coroutines.cancellation.CancellationException

data class NewCharacterData(val name: String, val age: UInt, val gender: Gender, val race: Race,
							val description: String, val stats: CharacterStats)

suspend fun promptNewCharacterInfo(player: Player): NewCharacterData {
	try {
		player.sendMessage(miniMessage.deserialize("<gray>Welcome, <yellow><player_name></yellow>!</gray>",
				Placeholder.unparsed("player_name", player.name)))

		player.sendMessage(miniMessage.deserialize("<gray>Let's create your brand new character!</gray>"))

		val name = player.promptChat(miniMessage.deserialize("<gray>What is the name of your character?</gray> " +
				"<dark_gray>(max 32 characters)</dark_gray>"))

		val age: UInt = flow {
			val message = miniMessage.deserialize("<gray>What is the age of <yellow><character_name></yellow>?</gray> " +
					"<dark_gray>(between 10 and 1000)</dark_gray>",
					Placeholder.unparsed("character_name", name))

			val num = player.promptChat(message).toUIntOrNull()
			if (num == null) throw IllegalArgumentException("Could not parse integer")
			if (num < 10U) throw IllegalArgumentException("Age must be at least 10")
			if (num > 1000U) throw IllegalArgumentException("Age may not be greater than 1000")
			emit(num)
		}.retry { if (it is IllegalArgumentException) { player.sendError(it.message ?: "unknown"); true } else false }
				.single()

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
				Race.values().asSequence(),
				{
					ItemStack(when (it) {
						Race.HUMAN -> Material.HAY_BLOCK
						Race.HIGH_ELF -> Material.PURPLE_TERRACOTTA
						Race.DARK_ELF -> Material.COPPER_BLOCK
						Race.WOOD_ELF -> Material.OAK_LOG
						Race.DWARF -> Material.DEEPSLATE_BRICKS
						Race.TIEFLING -> Material.MAGMA_BLOCK
						Race.ORC -> Material.MOSS_BLOCK
						Race.GOBLIN -> Material.LIGHT_GRAY_TERRACOTTA
						Race.HALFLING -> Material.CRAFTING_TABLE
					})
				},
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
						"<yellow><strength> Strength</yellow>, " +
						"</gray>",
				Placeholder.unparsed("strength", stats.strength.toString()),
				Placeholder.unparsed("defense", stats.defense.toString()),
				Placeholder.unparsed("agility", stats.agility.toString()),
				Placeholder.unparsed("intelligence", stats.intelligence.toString())))

		return NewCharacterData(name, age, gender, race, description, stats)
	} catch (e: CancellationException) {
		TODO()
	}
}
