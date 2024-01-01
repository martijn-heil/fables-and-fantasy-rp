package com.fablesfantasyrp.plugin.shops

object Permission {
	const val prefix = "fables.shops"
	const val Admin = "${prefix}.admin"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object Shop {
			private const val prefix = Command.prefix + ".shop"
			const val Destroy = "$prefix.destroy"
			const val Create = "$prefix.create"
			const val CreateOthers = "$prefix.create.others"
		}
	}
}