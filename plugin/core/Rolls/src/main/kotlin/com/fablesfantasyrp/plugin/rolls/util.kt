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
package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.location.location
import com.github.keelar.exprk.Expressions
import kotlin.random.Random
import kotlin.random.nextInt

fun roll(dice: UInt, character: Character?, kind: CharacterStatKind?): Pair<UInt, Int> {
	return roll(Random.Default, dice, character, kind)
}

fun roll(randomSource: Random, dice: UInt, character: Character?, kind: CharacterStatKind?): Pair<UInt, Int> {
	val world = character?.profile?.location?.world

	val stats = character?.totalStats
	val random: UInt = randomSource.nextInt(1..dice.toInt()).toUInt()

	var result = random.toInt()

	if (kind != null && stats != null) {
		result += kind.getRollModifierFor(stats[kind])
	}

	// Night lords character trait
	if (character != null &&
		kind != null &&
		!world!!.isDayTime &&
		character.traits.contains(CharacterTrait.NIGHT_LORDS)) result++

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

		// Otherwise the server will crash due to running out of memory or processing taking too long
		if (count > 100U) throw IllegalArgumentException("$count is too large of a number to process.")

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
