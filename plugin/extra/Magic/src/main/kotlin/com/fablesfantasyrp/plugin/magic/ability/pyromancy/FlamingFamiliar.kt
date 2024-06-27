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
package com.fablesfantasyrp.plugin.magic.ability.pyromancy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object FlamingFamiliar : MageAbility {
	override val id = "flaming_familiar"
	override val magicPath = MagicPath.PYROMANCY
	override val minimumMageLevel: Int = 3
	override val displayName: String = "Flaming Familiar"
	override val description: String =
			"The mage is capable of creating a small, flame-like spirit, which follows them around. " +
			"While being near their creator the spirit will grant them +1 to either their casting roll or their success roll. " +
			"However, the familiar can’t be used in combat and can't ignite anything. "

	override fun applyTo(mage: Mage) {

	}
}
