package com.fablesfantasyrp.plugin.profile.command

import com.fablesfantasyrp.plugin.profile.*
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.text.sendError
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Switch
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val profiles: ProfileRepository,
			   private val profileManager: ProfileManager) {
	inner class CommandProfile {
		@Command(aliases = ["list"], desc = "List profiles")
		@Require(Permission.Command.CommandProfile.List)
		fun list(@Sender sender: Player,
				 @CommandTarget(Permission.Command.CommandProfile.List + ".others") owner: OfflinePlayer) {
			sender.sendMessage("$SYSPREFIX ${owner.name} has the following profiles:")
			for (profile in profiles.allForOwner(owner)) {
				sender.sendMessage("${ChatColor.GRAY}#${profile.id}")
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
		fun transfer(@Sender sender: CommandSender, profile: Profile, to: OfflinePlayer) {
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
