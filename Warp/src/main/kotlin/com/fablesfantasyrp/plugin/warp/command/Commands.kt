package com.fablesfantasyrp.plugin.warp.command

import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.warp.Permission
import com.fablesfantasyrp.plugin.warp.SYSPREFIX
import com.fablesfantasyrp.plugin.warp.data.SimpleWarp
import com.fablesfantasyrp.plugin.warp.data.SimpleWarpRepository
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val warps: SimpleWarpRepository) {
	@Command(aliases = ["warp"], desc = "Warp to a place")
	@Require(Permission.Command.Warp)
	fun warp(@Sender sender: CommandSender, warp: SimpleWarp, @CommandTarget(Permission.Command.Warp + ".others") target: Player) {
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> Warping <player> to <warp></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.component("player", Component.text(target.name).style(target.nameStyle)),
				Placeholder.unparsed("warp", warp.id)
		))
		target.teleport(warp.location)
	}

	@Command(aliases = ["setwarp"], desc = "Set a warp")
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
	}

	@Command(aliases = ["delwarp"], desc = "Delete a warp")
	@Require(Permission.Command.DelWarp)
	fun delwarp(@Sender sender: CommandSender, warp: SimpleWarp) {
		warps.destroy(warp)
		sender.sendMessage("$SYSPREFIX Deleted warp ${warp.id}")
	}
}
