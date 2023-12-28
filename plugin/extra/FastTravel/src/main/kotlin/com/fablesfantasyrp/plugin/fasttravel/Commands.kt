package com.fablesfantasyrp.plugin.fasttravel

import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelLink
import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelLinkRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.parametric.annotation.Optional
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration
import kotlin.time.toKotlinDuration

class Commands(private val links: FastTravelLinkRepository) {
	class FastTravel(private val links: FastTravelLinkRepository) {
		@Command(aliases = ["list"], desc = "List fast travel links")
		@Require(Permission.Command.FastTravel.List)
		fun list(@Sender sender: CommandSender) {
			sender.sendMessage("$SYSPREFIX Regions linked for fast travel: \n" +
			links.all().map { "${it.from.region.id},${it.from.world.name} -> ${it.to.humanReadable()}" }
					.joinToString("\n"))
		}

		@Command(aliases = ["link"], desc = "Link a region to your current location for fast travel")
		@Require(Permission.Command.FastTravel.Link)
		fun link(@Sender sender: Player, region: WorldGuardRegion, @Optional("60s") travelDuration: Duration) {
			val old = links.forOriginRegion(region)
			if (old != null) {
				links.destroy(old)
			}

			links.create(FastTravelLink(
					id = 0,
					from = region,
					to = sender.location,
					travelDuration = travelDuration.toKotlinDuration()
			))
			sender.sendMessage("$SYSPREFIX Linked '${region.region.id},${region.world.name}' to your current location.")
		}

		@Command(aliases = ["unlink"], desc = "Unlink a region for fast travel")
		@Require(Permission.Command.FastTravel.Unlink)
		fun unlink(@Sender sender: Player, region: WorldGuardRegion) {
			val link = links.forOriginRegion(region) ?: return
			links.destroy(link)
		}
	}
}
