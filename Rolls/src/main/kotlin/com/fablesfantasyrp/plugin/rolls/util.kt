package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

fun CharacterStatKind.getRollModifierFor(statValue: UInt): Int = (min(statValue, 12U).toInt() / 2 * 2 - 6) / 2

fun roll(dice: UInt, kind: CharacterStatKind?, stats: CharacterStats?): Pair<UInt, UInt> {
	val random = Random.nextInt(1..dice.toInt()).toUInt()
	val result = if (kind != null && stats != null) random + kind.getRollModifierFor(stats[kind]).toUInt() else random
	return Pair(random, result)
}
