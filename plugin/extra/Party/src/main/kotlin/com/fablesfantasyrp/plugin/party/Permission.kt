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
