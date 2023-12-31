package com.fablesfantasyrp.plugin.shops.command

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.parametric.annotation.Switch
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.util.auth.AuthorizationException
import com.fablesfantasyrp.plugin.characters.displayName
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.shops.Permission
import com.fablesfantasyrp.plugin.shops.SYSPREFIX
import com.fablesfantasyrp.plugin.shops.ShopAuthorizer
import com.fablesfantasyrp.plugin.shops.ShopSlotCountCalculator
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.fancyName
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Instant

class ShopCommand(private val shops: ShopRepository,
				  private val authorizer: ShopAuthorizer,
				  private val slotCountCalculator: ShopSlotCountCalculator) {
	@Command(aliases = ["destroy"], desc = "Destroy a shop")
	@Require(Permission.Command.Shop.Destroy)
	suspend fun destroy(@Sender sender: CommandSender, shop: Shop) {
		shops.destroy(shop)
		sender.sendMessage("$SYSPREFIX Destroyed shop #${shop.id}")
	}

	@Command(aliases = ["create"], desc = "Create a shop using the item in your hand")
	@Require(Permission.Command.Shop.Create)
	suspend fun create(@Sender sender: Player,
					   @Switch('p') isPublic: Boolean,
					   @CommandTarget(Permission.Command.Shop.CreateOthers) owner: Profile) {
		if (isPublic && !authorizer.mayManagePublicShops(sender)) {
			throw AuthorizationException("You are not allowed to create a public shop")
		}

		val profile = if (isPublic) null else owner

		val targetBlock = sender.getTargetBlockExact(5) ?: run {
			sender.sendError("Please aim at a slab to create a shop")
			return
		}

		val item = sender.inventory.itemInMainHand
		if (item.isEmpty) {
			sender.sendError("Please hold an item in your hand to create the shop for")
			return
		}

		if (sender == profile?.owner) {
			val slots = slotCountCalculator.getShopSlots(sender)
			// TODO check shop slot count
		}

		val shop = shops.create(Shop(
			location = targetBlock.location.toBlockIdentifier(),
			owner = profile,
			amount = 1,
			buyPrice = 0,
			sellPrice = 0,
			item = item,
			stock = 0,
			lastActive = Instant.now(),
		))

		val itemDisplay = Component.text(item.asQuantity(2).fancyName).color(NamedTextColor.GREEN).hoverEvent(item.asHoverEvent())

		if (isPublic) {
			sender.sendMessage(miniMessage.deserialize("<gray><prefix> Created public shop #<id> selling <item></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("id", shop.id.toString()),
				Placeholder.component("item", itemDisplay))
			)
		} else {
			sender.sendMessage(miniMessage.deserialize("<gray><prefix> Created shop #<id> owned by <owner> selling <item></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("owner", owner.displayName()),
				Placeholder.unparsed("id", shop.id.toString()),
				Placeholder.component("item", itemDisplay))
			)
		}
	}
}
