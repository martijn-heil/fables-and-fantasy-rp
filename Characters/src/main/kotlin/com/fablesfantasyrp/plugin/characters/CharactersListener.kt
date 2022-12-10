package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.Race.HUMAN
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.playerinstance.PlayerSwitchPlayerInstanceEvent
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.isRealPlayer
import com.github.shynixn.mccoroutine.bukkit.launch
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.time.Instant

class CharactersListener(private val characters: EntityCharacterRepository) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchPlayerInstance(e: PlayerSwitchPlayerInstanceEvent) {
		val old = e.old ?: return
		val character = characters.forPlayerInstance(old) ?: return
		character.lastSeen = Instant.now()
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerInteractAtPlayer(e: PlayerInteractAtEntityEvent) {
		val target = e.rightClicked as? Player ?: return
		if (!target.isRealPlayer) return

		val character = target.currentPlayerCharacter ?: return
		e.player.sendMessage(characterCard(character))
	}

	@Suppress("DEPRECATION")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val legacyRaceCharacters = characters.forOwner(e.player).filter { it.race == HUMAN }
		if (legacyRaceCharacters.isEmpty()) return

		PLUGIN.launch {
			e.player.sendMessage(miniMessage.deserialize(
					"The human race has been split into their lore equivalent three races: " +
							"Attian Human, Khadan Human and Hinterlander Human. " +
							"You have <nchars> characters that are humans and for each of these you need to " +
							"select their new human race.",
					Placeholder.unparsed("nchars", legacyRaceCharacters.size.toString())
			).color(NamedTextColor.YELLOW))

			for (character in legacyRaceCharacters) {

			}
		}
	}
}
