package com.fablesfantasyrp.plugin.shops.command.provider

import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository

class ShopProvider(private val shops: ShopRepository) : Provider<Shop> {
	override val isProvided: Boolean = true

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Shop {
		TODO("Not yet implemented")
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		TODO("Not yet implemented")
	}
}
