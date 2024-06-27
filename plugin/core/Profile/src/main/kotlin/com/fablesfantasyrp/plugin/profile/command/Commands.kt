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
package com.fablesfantasyrp.plugin.profile.command

import com.fablesfantasyrp.plugin.profile.*
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.parametric.annotation.Optional
import com.fablesfantasyrp.caturix.parametric.annotation.Switch
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val profiles: ProfileRepository,
			   private val profileManager: ProfileManager) {
	inner class CommandProfile {
		@Command(aliases = ["list"], desc = "List profiles")
		@Require(Permission.Command.CommandProfile.List)
		fun list(@Sender sender: CommandSender,
				 @CommandTarget(Permission.Command.CommandProfile.List + ".others") owner: OfflinePlayer) {
			sender.sendMessage("$SYSPREFIX ${owner.name} has the following profiles:")
			for (profile in profiles.allForOwner(owner)) {
				sender.sendMessage("${ChatColor.GRAY}#${profile.id} ${ChatColor.DARK_PURPLE}${profile.description}")
			}
		}

		@Command(aliases = ["new"], desc = "Create a new profile")
		@Require(Permission.Command.CommandProfile.New)
		fun new(@Sender sender: CommandSender,
				@CommandTarget(Permission.Command.CommandProfile.New + ".others") owner: OfflinePlayer) {
			val result = profiles.create(Profile(
					owner = owner,
					isActive = true,
					description = null)
			)
			sender.sendMessage("$SYSPREFIX Created profile #${result.id} owned by ${owner.name}")
		}

		@Command(aliases = ["become"], desc = "Become a profile")
		@Require(Permission.Command.CommandProfile.Become)
		fun become(@Sender sender: CommandSender, @Switch('f') force: Boolean, profile: Profile, @CommandTarget target: Player) {
			try {
				profileManager.setCurrentForPlayer(target, profile, force = true)
				target.sendMessage("$SYSPREFIX You are now profile #${profile.id}")
				if (target != sender) sender.sendMessage("$SYSPREFIX ${target.name} is now profile #${profile.id}")
			} catch (ex: ProfileOccupiedException) {
				sender.sendError("This profile is currently occupied")
			}
		}

		@Command(aliases = ["transfer"], desc = "Transfer a profile")
		@Require(Permission.Command.CommandProfile.Transfer)
		fun transfer(@Sender sender: CommandSender, profile: Profile, @Optional to: OfflinePlayer?) {
			profile.owner = to
		}

		@Command(aliases = ["setactive"], desc = "Set active status of a profile")
		@Require(Permission.Command.CommandProfile.SetActive)
		fun setactive(@Sender sender: CommandSender, profile: Profile, value: Boolean) {
			profile.isActive = value
		}

		@Command(aliases = ["current"], desc = "Get current profile for a player")
		@Require(Permission.Command.CommandProfile.Current)
		fun current(@Sender sender: CommandSender, @CommandTarget target: Player) {
			val currentProfile = profileManager.getCurrentForPlayer(target)
			if (currentProfile != null) {
				sender.sendMessage("$SYSPREFIX ${target.name} is currently profile #${currentProfile.id}")
			} else {
				sender.sendMessage("$SYSPREFIX ${target.name} is currently not a profile")
			}
		}
	}
}
