package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.Services
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.Material

fun getSpellCastingBonus(path: MagicPath, level: Int): Int {
	return when (level) {
		in 2..4 -> 1
		in 5..6 -> 2
		in 7..9 -> 3
		10 -> 4
		else -> 0
	}
}

val SpellEffectiveness.color get() = when(this) {
	SpellEffectiveness.CRITICAL_FAILURE -> NamedTextColor.DARK_RED
	SpellEffectiveness.FAILURE -> NamedTextColor.RED
	SpellEffectiveness.SUCCESS -> NamedTextColor.GREEN
	SpellEffectiveness.CRITICAL_SUCCESS -> NamedTextColor.DARK_GREEN
}

fun spellCard(spell: SpellData)= miniMessage.deserialize(
			"<dark_purple><spell_name></dark_purple><newline>" +
					"<gray><italic><spell_description></italic></gray>",
			Placeholder.unparsed("spell_name", spell.displayName),
			Placeholder.unparsed("spell_description", spell.description))

fun spellDisplay(spell: SpellData): Component = Component.text(spell.displayName)
		.color(NamedTextColor.DARK_PURPLE)
		.hoverEvent(HoverEvent.showText(spellCard(spell)))

fun getSpellCastingMessage(playerCharacter: Character,
						   spell: SpellData,
						   castingRoll: Int,
						   effectiveness: SpellEffectiveness): Component {
	val profileManager = Services.get<ProfileManager>()
	val player = profileManager.getCurrentForProfile(playerCharacter.profile)!!

	val resultMessage = Component.text(effectiveness.displayName).color(effectiveness.color)

	return miniMessage.deserialize(
			"<yellow><character_name></yellow> attempts to cast <spell_name> " +
					"and gets a <result> with casting roll value <casting_roll>.",
			Placeholder.unparsed("casting_roll", "$castingRoll"),
			Placeholder.unparsed("character_name", playerCharacter.name),
			Placeholder.component("spell_name", spellDisplay(spell)),
			Placeholder.component("result", resultMessage)).style(player.chat.chatStyle ?: Style.style(NamedTextColor.YELLOW))
}

fun calculateTearLocation(eyeLocation: Location): Location? {
	val radius = 1
	val nearbyPlayers = getPlayersWithinRange(eyeLocation, radius.toUInt())

	fun filterUndesiredLocations(s: Sequence<Location>): Collection<Location> {
		return s.filter { it.block.type == Material.AIR }
				.filter {
					nearbyPlayers.find { p -> p.eyeLocation.toBlockLocation() != it &&
							p.location.toBlockLocation() != it } == null
				}.toList()
	}

	val cylPreferred = ArrayList<Location>()
	val cylAlternative = ArrayList<Location>()

	for (deltaX in -radius..radius) {
		for (deltaZ in -radius .. radius) {
			if (deltaX == 0 && deltaZ == 0) continue
			cylPreferred.add(eyeLocation.clone().add(deltaX.toDouble(), 1.00, deltaZ.toDouble()).toBlockLocation())
			cylAlternative.add(eyeLocation.clone().add(deltaX.toDouble(), 0.00, deltaZ.toDouble()).toBlockLocation())
		}
	}

	return filterUndesiredLocations(cylPreferred.asSequence()).randomOrNull() ?: run {
		filterUndesiredLocations(cylAlternative.asSequence()).randomOrNull()
	}
}
