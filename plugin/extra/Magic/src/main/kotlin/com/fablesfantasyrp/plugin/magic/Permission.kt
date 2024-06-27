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
package com.fablesfantasyrp.plugin.magic

internal object Permission {
	const val prefix = "fables.magic"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Castspell = "$prefix.castspell"
		const val Opentear = "$prefix.opentear"
		const val Closetear = "$prefix.closetear"
		const val Spellbook = "$prefix.spellbook"
		const val Resetspellbook = "$prefix.resetspellbook"
		const val Setmagicpath = "$prefix.setmagictype"
		const val Setmagiclevel = "$prefix.setmagiclevel"
		const val Tears = "$prefix.tears"

		object Ability {
			private const val prefix = Command.prefix + ".ability"
			const val List = "$prefix.list"
			const val Activate = "$prefix.activate"
			const val Deactiviate = "$prefix.deactivate"
		}
	}
}
