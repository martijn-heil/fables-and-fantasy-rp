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
package com.fablesfantasyrp.plugin.characters

object Permission {
	const val prefix = "fables.characters"
	const val Staff = "${prefix}.staff"
	const val Admin = "${prefix}.admin"
	const val Any = "${prefix}.any"

	object Change {
		private const val prefix = Permission.Command.Characters.prefix + ".change"
		const val Description = "${prefix}.description"
		const val DateOfBirth = "${prefix}.dateofbirth"
		const val Name = "${prefix}.name"
		const val Stats = "${prefix}.stats"
		const val Race = "${prefix}.race"
		const val Gender = "${prefix}.gender"
		const val Traits = "${prefix}.traits"
	}

	object Command {
		const val prefix = Permission.prefix + ".command"

		object Characters {
			const val prefix = Permission.Command.prefix + ".characters"
			const val New = "${prefix}.new"
			const val List = "${prefix}.list"
			const val Listunowned = "${prefix}.listunowned"
			const val Card = "${prefix}.card"
			const val Kill = "${prefix}.kill"
			const val Resurrect = "${prefix}.resurrect"
			const val Shelf = "${prefix}.shelf"
			const val Unshelf = "${prefix}.unshelf"
			const val SetRace = "${prefix}.setrace"
			const val SetAge = "${prefix}.setage"
			const val Become = "${prefix}.become"
			const val Transfer = "${prefix}.transfer"

			object Stats {
				private const val prefix = Permission.Command.Characters.prefix + ".stats"
				const val Set = "${prefix}.set"
				const val Edit = "${prefix}.edit"
			}
		}

		object CharacterTrait {
			const val prefix = Permission.Command.prefix + ".charactertrait"
			const val List = "${prefix}.list"
			const val Assign = "${prefix}.assign"
			const val Revoke = "${prefix}.revoke"
		}
	}
}
