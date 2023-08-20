package com.fablesfantasyrp.plugin.charactertraits

object Permission {
	const val prefix = "fables.charactertraits"

	object Command {
		const val prefix = Permission.prefix + ".command"

		object CharacterTrait {
			const val prefix = Permission.Command.prefix + ".charactertrait"
			const val List = "${prefix}.list"
			const val Assign = "${prefix}.assign"
			const val Revoke = "${prefix}.revoke"
		}
	}
}
