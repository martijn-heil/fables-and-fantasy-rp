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
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
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
			Placeholder.component("status", (!newValue).asEnabledDisabledComponent()),
			Placeholder.component("player", Component.text(target.name).style(target.nameStyle))
		))
	}
}
