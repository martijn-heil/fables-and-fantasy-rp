package com.fablesfantasyrp.plugin.whitelist.command

import com.fablesfantasyrp.plugin.whitelist.Permission
import com.fablesfantasyrp.plugin.whitelist.joinMessage
import com.fablesfantasyrp.plugin.whitelist.quitMessage
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["fjoin", "flogin", "fakejoin", "fakelogin"], desc = "Send a fake login message")
	@Require(Permission.SilentJoinQuit)
	fun fakejoin(@Sender sender: Player) {
		val message = joinMessage(sender, isSilent = false) ?: return
		if (message.recipients != null) {
			message.recipients.forEach { it.sendMessage(message.message) }
		} else {
			Bukkit.broadcast(message.message)
		}
	}

	@Command(aliases = ["fquit", "flogout", "fleave", "fakequit", "fakelogout", "fakeleave"], desc = "Send a fake quit message")
	@Require(Permission.SilentJoinQuit)
	fun fakequit(@Sender sender: Player) {
		val message = quitMessage(sender, isSilent = false) ?: return
		if (message.recipients != null) {
			message.recipients.forEach { it.sendMessage(message.message) }
		} else {
			Bukkit.broadcast(message.message)
		}
	}
}
