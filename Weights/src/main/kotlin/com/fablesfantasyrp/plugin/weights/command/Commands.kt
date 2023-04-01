package com.fablesfantasyrp.plugin.weights.command

import com.fablesfantasyrp.plugin.weights.Permission
import com.fablesfantasyrp.plugin.weights.WeightsConfig
import com.fablesfantasyrp.plugin.weights.gui.WeightsGui
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Commands(private val plugin: JavaPlugin, private val weightsConfig: WeightsConfig) {
	@Command(aliases = ["weights"], desc = "See your equipment weights.")
	@Require(Permission.Command.Weights)
	fun weights(@Sender sender: Player) {
		WeightsGui(plugin, sender.inventory, weightsConfig).show(sender)
	}
}
