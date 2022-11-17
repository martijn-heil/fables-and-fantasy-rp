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
			private const val prefix = Permission.prefix + ".ability"
			const val List = "$prefix.list"
			const val Activate = "$prefix.activate"
			const val Deactiviate = "$prefix.deactivate"
		}
	}
}
