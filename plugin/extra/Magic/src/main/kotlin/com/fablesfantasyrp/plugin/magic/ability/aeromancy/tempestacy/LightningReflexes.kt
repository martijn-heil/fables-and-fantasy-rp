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
package com.fablesfantasyrp.plugin.magic.ability.aeromancy.tempestacy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object LightningReflexes : MageAbility {
	override val id = "lightning_reflexes"
	override val magicPath = MagicPath.TEMPESTACY
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Lightning Reflexes"
	override val description: String =
			"When entering combat, the wind mage of the highest level always goes first, " +
					"except if a 1 was rolled in a major fight. If there are several air mages in one combat session, " +
					"they will all go before the other people in order of what was rolled. " +
					"This also makes the aeromancer’s common reflexes better, which can be used in roleplay situations. "

	override fun applyTo(mage: Mage) {

	}
}
