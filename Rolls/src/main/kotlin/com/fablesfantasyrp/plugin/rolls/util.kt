package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.characters.CharacterStatKind
import kotlin.math.min

fun CharacterStatKind.getRollModifierFor(statValue: UInt): Int = (min(statValue, 12U).toInt() / 2 * 2 - 6) / 2
