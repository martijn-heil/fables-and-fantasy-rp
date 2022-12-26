package com.fablesfantasyrp.plugin.economy

object Permission {
	const val prefix = "fables.economy"
	const val PayOwn = "${prefix}.payown"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Balance = "${Command.prefix}.balance"
		const val Pay = "${Command.prefix}.pay"
		object Eco {
			private const val prefix = Permission.Command.prefix + ".eco"
			const val Give = "${Command.Eco.prefix}.give"
			const val Take = "${Command.Eco.prefix}.take"
			const val Set = "${Command.Eco.prefix}.set"
		}

		object Bank {
			private const val prefix = Permission.Command.prefix + ".bank"
			const val Open = "${Command.Bank.prefix}.open"
		}
	}
}
