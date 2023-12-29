package com.fablesfantasyrp.plugin.shops.command

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.plugin.shops.Permission
import com.fablesfantasyrp.plugin.shops.SYSPREFIX
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import org.bukkit.command.CommandSender

class ShopCommand(private val shops: ShopRepository) {
	@Command(aliases = ["destroy"], desc = "Destroy a shop")
	@Require(Permission.Command.Shop.Destroy)
	fun destroy(@Sender sender: CommandSender, shop: Shop) {
		shops.destroy(shop)
		sender.sendMessage("$SYSPREFIX Destroyed shop #${shop.id}")
	}
}
