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
package com.fablesfantasyrp.plugin.characters.service.implementation

import com.fablesfantasyrp.plugin.characters.dal.enums.CreatureSize
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.service.api.CreatureSizeCalculator

class CreatureSizeCalculatorImpl() : CreatureSizeCalculator {
	override fun getCreatureSize(character: Character): CreatureSize {
		return if (
			character.traits.contains(CharacterTrait.ABNORMALLY_TALL) ||
			character.traits.contains(CharacterTrait.HULKING_BRUTE)) {
			CreatureSize.LARGE
		} else {
			character.race.creatureSize
		}
	}
}
