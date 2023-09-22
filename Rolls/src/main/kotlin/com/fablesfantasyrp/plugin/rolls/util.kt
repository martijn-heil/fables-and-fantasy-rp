package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.location.location
import com.github.keelar.exprk.Expressions
import org.koin.core.context.GlobalContext
import kotlin.random.Random
import kotlin.random.nextInt

fun roll(dice: UInt, character: Character?, kind: CharacterStatKind?): Pair<UInt, Int> {
	val traits = GlobalContext.get().get<CharacterTraitRepository>()
	val trait = traits.forId(KnownCharacterTraits.NIGHT_LORDS)
	val world = character?.profile?.location?.world

	val stats = character?.totalStats
	val random: UInt = Random.nextInt(1..dice.toInt()).toUInt()

	var result = random.toInt()

	if (kind != null && stats != null) {
		result += kind.getRollModifierFor(stats[kind])
	}

	// Night lords character trait
	if (character != null &&
		kind != null &&
		!world!!.isDayTime &&
		trait != null &&
		traits.hasTrait(character, trait)) result++

	return Pair(random, result)
}

fun Character.roll(dice: UInt, kind: CharacterStatKind?): Int {
	return roll(dice, this, kind).second
}

fun parseRollExpression(expression: String, character: Character?, defaultKind: CharacterStatKind?): String {
	val regex = Regex("([0-9]*)d([0-9]+)([a-zA-Z]?)")
	return expression.replace(regex) {
		val count = (if (it.groupValues[1] != "") it.groupValues[1] else "1").toUIntOrNull() ?:
			throw IllegalArgumentException("Invalid syntax")

		val statKind = when(it.groupValues[3].lowercase()) {
			"i" -> CharacterStatKind.INTELLIGENCE
			"s" -> CharacterStatKind.STRENGTH
			"d" -> CharacterStatKind.DEFENSE
			"a" -> CharacterStatKind.AGILITY
			"" -> defaultKind
			else -> throw IllegalArgumentException("Invalid character stat kind '${it.groupValues[3]}'")
		}

		if (statKind != null && character == null) {
			throw IllegalStateException("You cannot roll a specific stat kind while you are out of character")
		}

		val dice = it.groupValues[2].toUIntOrNull() ?: throw IllegalArgumentException("Invalid syntax")

		(1U..count).map { roll(dice, character, statKind) }.sumOf { it.second }.toString()
	}
}

fun rollExpression(expression: String, character: Character?, defaultKind: CharacterStatKind?): Int {
	val parsed = parseRollExpression(expression, character, defaultKind)
	return Expressions().eval(parsed).toInt()
}
