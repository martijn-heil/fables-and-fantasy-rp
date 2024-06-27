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
package com.fablesfantasyrp.plugin.magic.ability.aeromancy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object Cloud : MageAbility {
	override val id = "cloud"
	override val magicPath = MagicPath.AEROMANCY
	override val minimumMageLevel: Int = 3
	override val displayName: String = "Cloud"
	override val description: String =
			"The caster can create a cloud to sit on, giving the user a +1 to any spell roll," +
			" but making their movement 0. Sitting on this cloud will heal small cuts and bruises slowly," +
			" which is not applicable in combat situations."

	override fun applyTo(mage: Mage) {

	}
}
