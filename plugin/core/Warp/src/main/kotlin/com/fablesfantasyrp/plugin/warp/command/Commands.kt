package com.fablesfantasyrp.plugin.warp.command

import com.fablesfantasyrp.plugin.morelogging.StaffActionBroadcaster
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.warp.Permission
import com.fablesfantasyrp.plugin.warp.SYSPREFIX
import com.fablesfantasyrp.plugin.warp.data.SimpleWarp
import com.fablesfantasyrp.plugin.warp.data.SimpleWarpRepository
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val warps: SimpleWarpRepository, private val broadcaster: StaffActionBroadcaster) {
	@Command(aliases = ["warp", "fwarp"], desc = "Warp to a place")
	@Require(Permission.Command.Warp)
	fun warp(@Sender sender: CommandSender, warp: SimpleWarp, @CommandTarget(Permission.Command.Warp + ".others") target: Player) {
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Warping <player> to <warp></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.component("player", Component.text(target.name).style(target.nameStyle)),
				Placeholder.unparsed("warp", warp.id)
		))
		target.teleport(warp.location)
		broadcaster.log(sender, "Warped ${target.name} to ${warp.id}")
	}

	@Command(aliases = ["setwarp", "fsetwarp"], desc = "Set a warp")
	@Require(Permission.Command.SetWarp)
	fun setwarp(@Sender sender: Player, name: String) {
		val finalName = name.lowercase().replace(' ', '_')
		if (!warps.isSyntacticallyValidId(finalName)) {
			sender.sendError("'$finalName' is not a valid warp name.")
			return
		}

		val warp = SimpleWarp(id = finalName, location = sender.location)

		if (warps.exists(finalName)) {
			warps.update(warp)
		} else {
			warps.create(warp)
		}

		sender.sendMessage("$SYSPREFIX Set warp ${warp.id} to your current location.")
		broadcaster.log(sender, "Set warp ${warp.id} to their current location.")
	}

	@Command(aliases = ["delwarp", "fdelwarp"], desc = "Delete a warp")
	@Require(Permission.Command.DelWarp)
	fun delwarp(@Sender sender: CommandSender, warp: SimpleWarp) {
		warps.destroy(warp)
		sender.sendMessage("$SYSPREFIX Deleted warp ${warp.id}")
		broadcaster.log(sender, "Deleted warp ${warp.id}")
	}
}
