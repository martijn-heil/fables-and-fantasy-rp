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
import com.fablesfantasyrp.plugin.utils.command.OrVisual
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
	suspend fun destroy(@Sender sender: CommandSender, @OrVisual shop: Shop) {
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

		if (!isPublic && sender == profile?.owner) {
			val slots = slotCountCalculator.getShopSlots(sender)
			if (slots != null) {
				val usedSlots = shops.forOwner(profile).size
				if (usedSlots >= slots) {
					sender.sendError("You have already used ${slots}/${slots} of your shop tiles. Destroy another shop first.")
					return
				}
			}
		}

		val blockIdentifier = targetBlock.location.toBlockIdentifier()

		if (shops.forLocation(blockIdentifier) != null) {
			sender.sendError("A shop already exists here")
			return
		}

		if (!authorizer.mayCreateShopAt(sender, blockIdentifier)) {
			throw AuthorizationException("You are not allowed to make a shop here, you need build permissions")
		}

		val shop = shops.create(Shop(
			location = blockIdentifier,
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
