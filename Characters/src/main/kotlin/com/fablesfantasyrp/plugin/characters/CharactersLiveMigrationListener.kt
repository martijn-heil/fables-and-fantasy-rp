package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.form.promptChat
import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.title.Title
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.util.*

class CharactersLiveMigrationListener(private val plugin: Plugin,
									  private val characters: EntityCharacterRepository) : Listener {
	private val server = plugin.server
	private val blockMovement = HashSet<UUID>()

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerMove(e: PlayerMoveEvent) {
		if (blockMovement.contains(e.player.uniqueId)) e.isCancelled = true
	}

	@Suppress("DEPRECATION")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val player = e.player
		if (!player.isWhitelisted) return
		val legacyRaceCharacters = characters.forOwner(player).filter { it.race == Race.HUMAN }
		if (legacyRaceCharacters.isEmpty()) return

		PLUGIN.launch {
			val titleJob = server.scheduler.scheduleSyncRepeatingTask(plugin, {
				e.player.showTitle(Title.title(Component.text("Welcome back!").color(NamedTextColor.LIGHT_PURPLE),
						Component.text("Please follow the instructions in chat").color(NamedTextColor.YELLOW),
						Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(2))))
			}, 0, 20)

			blockMovement.add(player.uniqueId)
			try {
				delay(100)
				val supportDiscord = "https://discord.gg/Qw6Nzmtpqc"

				player.sendMessage(miniMessage.deserialize(
						"The human race has been split into their lore equivalent three races: " +
								"Attian Human, Khadan Human and Hinterlander Human. " +
								"You have <nchars> characters that are humans and for each of these you need to " +
								"select their correct human race.",
						Placeholder.unparsed("nchars", legacyRaceCharacters.size.toString())
				).color(NamedTextColor.YELLOW))

				player.sendMessage(miniMessage.deserialize(
						"If you have any questions, please open a <green>General Question ticket</green> " +
								"in our Support Discord <support_discord>",
						Placeholder.component("support_discord", parseLinks(supportDiscord))).color(NamedTextColor.LIGHT_PURPLE))

				for (character in legacyRaceCharacters) {
					do {
						val ok = player.promptChat(miniMessage.deserialize(
									"Please write '<gray>continue</gray>' in chat to select the correct " +
										"human race for <green><character_name></green>",
							Placeholder.unparsed("character_name", character.name)).color(NamedTextColor.YELLOW))
					} while (ok != "continue")

					while (true) {
						try {
							val newRace = player.promptGui(GuiSingleChoice<Race>(PLUGIN, "${character.name}'s race",
									sequenceOf(Race.ATTIAN_HUMAN, Race.KHADAN_HUMAN, Race.HINTERLANDER_HUMAN),
									{ ItemStack(Material.HAY_BLOCK) },
									{ "${ChatColor.GOLD}$it" }))
							character.race = newRace
							break
						} catch (_: CancellationException) {}
					}
				}

				player.sendMessage("${ChatColor.YELLOW}All done, thank you for your patience!")
				player.sendMessage(miniMessage.deserialize(
						"<light_purple>If you have any questions or think you made a mistake, " +
								"please open a <green>General Question ticket</green> in our Support Discord <support_discord></light_purple>",
						Placeholder.component("support_discord", parseLinks(supportDiscord))))
			} finally {
				blockMovement.remove(player.uniqueId)
				server.scheduler.cancelTask(titleJob)
			}
		}
	}
}
