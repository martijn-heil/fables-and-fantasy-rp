package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.github.keelar.exprk.Expressions
import kotlin.random.Random
import kotlin.random.nextInt

fun roll(dice: UInt, kind: CharacterStatKind?, stats: CharacterStats?): Pair<UInt, Int> {
	val random: UInt = Random.nextInt(1..dice.toInt()).toUInt()
	val result: Int = if (kind != null && stats != null) random.toInt() + kind.getRollModifierFor(stats[kind]) else random.toInt()
	return Pair(random, result)
}

fun parseRollExpression(expression: String, stats: CharacterStats?, defaultKind: CharacterStatKind?): String {
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

		if (statKind != null && stats == null) {
			throw IllegalStateException("You cannot roll a specific stat kind while you are out of character")
		}

		val dice = it.groupValues[2].toUIntOrNull() ?: throw IllegalArgumentException("Invalid syntax")

		(1U..count).map { roll(dice, statKind, stats) }.sumOf { it.second }.toString()
	}
}

fun rollExpression(expression: String, stats: CharacterStats?, defaultKind: CharacterStatKind?): Int {
	val parsed = parseRollExpression(expression, stats, defaultKind)
	return Expressions().eval(parsed).toInt()
}
