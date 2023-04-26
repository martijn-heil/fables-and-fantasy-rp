package com.fablesfantasyrp.plugin.morelogging

import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

interface StaffActionBroadcaster {
	fun log(by: CommandSender, message: Component)
	fun log(by: CommandSender, message: String) = this.log(by, Component.text(message))
}
