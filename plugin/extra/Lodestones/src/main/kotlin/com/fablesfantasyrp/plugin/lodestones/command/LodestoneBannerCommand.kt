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
package com.fablesfantasyrp.plugin.lodestones.command

import com.fablesfantasyrp.plugin.lodestones.Permission
import com.fablesfantasyrp.plugin.lodestones.SYSPREFIX
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Tag
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LodestoneBannerCommand(private val banners: LodestoneBannerRepository) {
	@Command(aliases = ["list"], desc = "List lodebanners")
	@Require(Permission.Command.Lodebanner.List)
	fun list(@Sender sender: CommandSender) {
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Lodebanners:<newline><banners></gray>",
			Placeholder.component("prefix", legacyText(SYSPREFIX)),
			Placeholder.component("banners",
				Component.join(JoinConfiguration.newlines(),
					banners.all().map { Component.text("#${it.id} to ${it.lodestone.name} ${it.location.toLocation().humanReadable()}" ) }
				)
			)
		))
	}

	@Command(aliases = ["destroy"], desc = "Destroy lodebanner")
	@Require(Permission.Command.Lodebanner.Destroy)
	fun destroy(@Sender sender: CommandSender, lodebanner: LodestoneBanner) {
		banners.destroy(lodebanner)
		sender.sendMessage("$SYSPREFIX Destroyed lodebanner #${lodebanner.id}")
	}

	@Command(aliases = ["create"], desc = "Create a lodebanner")
	@Require(Permission.Command.Lodebanner.Create)
	fun create(@Sender sender: Player, lodestone: Lodestone) {
		val block = sender.getTargetBlock(10)
		if (block == null || !Tag.BANNERS.isTagged(block.type)) {
			sender.sendError("Please aim at a banner.")
			return
		}

		val banner = banners.create(LodestoneBanner(
			id = 0,
			location = block.location.toBlockIdentifier(),
			lodestone = lodestone,
		))

		sender.sendMessage("$SYSPREFIX Created lodebanner #${banner.id}")
	}
}
