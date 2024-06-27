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
package com.fablesfantasyrp.plugin.magic.ability.aeromancy.lunarmancy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object CleansingTouch : MageAbility {
	override val id = "cleansing_touch"
	override val magicPath = MagicPath.LUNARMANCY
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Cleansing Touch"
	override val description: String =
			"Lunarmancers can cleanse corruption of objects and people. " +
			"This means they can heal people tainted by voidal energies and can heal voidal mages. " +
			"A Lunarmancer can decrease a voidal mages' level by one per OOC week until they are cleansed of voidal energies. " +
			"This can only happen if the voidal mage allows this to happen. "

	override fun applyTo(mage: Mage) {

	}
}
