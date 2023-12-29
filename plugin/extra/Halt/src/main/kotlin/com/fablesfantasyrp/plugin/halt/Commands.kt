package com.fablesfantasyrp.plugin.halt

import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import org.bukkit.ChatColor.GRAY
import org.bukkit.ChatColor.RED
import org.bukkit.GameMode.SPECTATOR
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Commands(private val plugin: Plugin,
			   private val characters: CharacterRepository,
			   private val profileManager: ProfileManager) {
	@Command(aliases = ["halt", "h"], desc = "Formally halt nearby players")
	@Require("fables.halt.command.halt")
	fun halt(@Sender sender: Character, @AllowCharacterName target: Player) {
		flaunch {
			val senderPlayer = profileManager.getCurrentForProfile(sender.profile)!!

			val targets = listOf(target).asSequence()
				.filter { it.gameMode != SPECTATOR }
				.filter { !it.isVanished }
				.toList()

			for (it in targets) {
				val profile = profileManager.getCurrentForPlayer(it)
				val character = profile?.let { characters.forProfile(profile) }

				if (character == null) {
					senderPlayer.sendMessage("$SYSPREFIX ${RED}Failed to halt ${GRAY}${it.name} ${RED}" +
						"because this player is not in-character.")
					continue
				}

				if (it == senderPlayer) {
					senderPlayer.sendMessage("$SYSPREFIX ${RED}You can't halt yourself!")
					continue
				}

				if (it.location.distanceSafe(sender.profile.location) > 15) {
					senderPlayer.sendMessage("$SYSPREFIX ${RED}Failed to halt ${GRAY}${character.name}${RED} " +
						"because this player is too far away!")
					continue
				}

				if (it.isInvulnerable) {
					senderPlayer.sendMessage("$SYSPREFIX ${RED}Failed to halt ${GRAY}${character.name}${RED} " +
						"because this player is in god mode!")
					continue
				}

				character.halt(sender)
			}
		}
	}
}
