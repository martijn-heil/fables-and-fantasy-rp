package com.fablesfantasyrp.plugin.weights.command

import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.nameStyle
import com.fablesfantasyrp.plugin.utils.asEnabledDisabledComponent
import com.fablesfantasyrp.plugin.weights.ItemPickupManager
import com.fablesfantasyrp.plugin.weights.Permission
import com.fablesfantasyrp.plugin.weights.SYSPREFIX
import com.fablesfantasyrp.plugin.weights.WeightsConfig
import com.fablesfantasyrp.plugin.weights.gui.WeightsGui
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Commands(private val plugin: JavaPlugin,
			   private val weightsConfig: WeightsConfig,
			   private val itemPickupManager: ItemPickupManager) {
	@Command(aliases = ["weights"], desc = "See your equipment weights.")
	@Require(Permission.Command.Weights)
	fun weights(@Sender sender: Player) {
		WeightsGui(plugin, sender.inventory, weightsConfig).show(sender)
	}

	@Command(aliases = ["tipu", "toggleitempickup"], desc = "Toggle picking up items")
	@Require(Permission.Command.Tipu)
	fun tipu(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Tipu + ".others") target: Player) {
		val newValue = !itemPickupManager.hasPickupDisabled(target)
		itemPickupManager.setPickupDisabled(target, newValue)
		sender.sendMessage(miniMessage.deserialize("<gray><prefix> <status> item pickup for <player></gray>",
			Placeholder.component("prefix", legacyText(SYSPREFIX)),
			Placeholder.component("status", newValue.asEnabledDisabledComponent()),
			Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
		))
	}
}
