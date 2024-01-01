package com.fablesfantasyrp.plugin.shops.command.provider

import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.parametric.ProvisionException
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import org.bukkit.entity.Player

class OrVisualShopProvider(private val shops: ShopRepository,
						   private val shopProvider: Provider<Shop>,
						   private val senderProvider: Provider<Player>) : Provider<Shop> {
	override val isProvided: Boolean = false

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Shop {
		return if (arguments.hasNext()) {
			shopProvider.get(arguments, modifiers)
		} else {
			val sender = senderProvider.get(arguments, modifiers)
			val block = sender.getTargetBlockExact(5)
				?: throw ProvisionException("Please aim at a shop or provide a shop identifier")

			shops.forLocation(block.location.toBlockIdentifier())
				?: throw ProvisionException("Please aim at a shop or provide a shop identifier")
		}
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
