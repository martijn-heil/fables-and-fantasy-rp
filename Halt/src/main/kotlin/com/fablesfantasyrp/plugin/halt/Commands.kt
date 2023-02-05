package com.fablesfantasyrp.plugin.halt

import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.ess
import com.fablesfantasyrp.plugin.utils.isVanished
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.ChatColor.GRAY
import org.bukkit.ChatColor.RED
import org.bukkit.GameMode.SPECTATOR
import org.bukkit.entity.Player

class Commands(private val characters: EntityCharacterRepository,
			   private val profileManager: ProfileManager) {
	@Command(aliases = ["halt", "h"], desc = "Formally halt nearby players")
	@Require("fables.halt.command.halt")
	fun halt(@Sender sender: Character, @AllowCharacterName target: Player) {
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

			if (it.location.distance(sender.location) > 15) {
				senderPlayer.sendMessage("$SYSPREFIX ${RED}Failed to halt ${GRAY}${character.name}${RED} " +
						"because this player is too far away!")
				continue
			}

			if (it.ess.isGodModeEnabled) {
				senderPlayer.sendMessage("$SYSPREFIX ${RED}Failed to halt ${GRAY}${character.name}${RED} " +
						"because this player is in god mode!")
				continue
			}

			character.halt(sender)
		}
	}
}
