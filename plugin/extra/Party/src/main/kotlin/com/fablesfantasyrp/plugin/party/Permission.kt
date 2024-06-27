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
package com.fablesfantasyrp.plugin.party

object Permission {
	const val prefix = "fables.party"
	const val Admin = "$prefix.admin"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object Party {
			private const val prefix = Permission.Command.prefix + ".party"
			const val List = "${prefix}.list"
			const val Create = "${prefix}.create"
			const val Transfer = "${prefix}.transfer"
			const val Disband = "${prefix}.disband"
			const val Members = "${prefix}.members"
			const val Card = "${prefix}.card"
			const val Kick = "${prefix}.kick"
			const val Leave = "${prefix}.leave"
			const val Invite = "${prefix}.invite"
			const val InviteNear = "${prefix}.invitenear"
			const val Invites = "${prefix}.invites"
			const val Uninvite = "${prefix}.uninvite"
			const val Join = "${prefix}.join"
			const val Name = "${prefix}.name"
			const val Setspawn = "${prefix}.setspawn"
			const val Setrespawns = "${prefix}.setrespawns"
			const val Togglerespawns = "${prefix}.togglerespawns"
			const val Select = "${prefix}.select"
			const val Spectate = "${prefix}.spectate"
			const val Color = "${prefix}.color"
		}
	}
}
