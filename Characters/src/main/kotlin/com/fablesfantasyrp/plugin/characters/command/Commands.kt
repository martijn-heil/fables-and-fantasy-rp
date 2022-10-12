package com.fablesfantasyrp.plugin.characters.command

import com.denizenscript.denizen.objects.PlayerTag
import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.characters.FablesCharacters
import com.fablesfantasyrp.plugin.characters.Permission
import com.fablesfantasyrp.plugin.characters.SYSPREFIX
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.characters.gui.CharacterStatsGui
import com.fablesfantasyrp.plugin.characters.playerCharacters
import com.fablesfantasyrp.plugin.denizeninterop.denizenRun
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class Commands(private val plugin: SuspendingJavaPlugin) {
	@Command(aliases = ["cardother"], desc = "Show another character's card")
	@Require(Permission.Command.Cardother)
	fun cardother(@Sender sender: Player, target: PlayerCharacterData) {
		denizenRun("characters_print_card", mapOf(
				Pair("player", PlayerTag(sender)),
				Pair("target", PlayerTag(target.player)),
				Pair("id", ElementTag(target.id.toLong())),
		))
	}

	@Command(aliases = ["listcharacters", "listchars"], desc = "List a player's characters")
	@Require(Permission.Command.Listcharacters)
	fun listcharacters(@Sender sender: Player, @CommandTarget(Permission.Command.Listcharacters + ".others") target: OfflinePlayer) {
		val msg = Component.text()

		val header = miniMessage.deserialize("<gray><prefix><player_name> has the following characters:</gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("player_name", target.name ?: target.uniqueId.toString())
		)
		msg.append(header)
		msg.append(Component.newline())

		val body = Component.text().append(target.playerCharacters.asSequence().map {
			miniMessage.deserialize("<gray>#<id> <name></gray>",
					Placeholder.unparsed("id", it.id.toString().padStart(4, '0')),
					Placeholder.unparsed("name", it.name))
		}.join(Component.newline()).asIterable()).asComponent()
		msg.append(body)

		sender.sendMessage(msg.asComponent().compact())
	}

	@Command(aliases = ["updatestats"], desc = "Update a player's character stats")
	@Require(Permission.Command.Updatestats)
	fun updatestats(@Sender sender: Player, target: PlayerCharacterData) {
		val minimums = target.race.boosters + CharacterStats(2U, 2U, 2U, 2U)
		var initialSliderValues = target.stats - minimums
		if (initialSliderValues.strength > 8U ||
				initialSliderValues.defense > 8U ||
				initialSliderValues.agility > 8U ||
				initialSliderValues.intelligence > 8u) {
			sender.sendMessage("$SYSPREFIX Detected that you will be editing legacy player stats, starting with a clean slate.")
			initialSliderValues = CharacterStats(0U, 0U, 0U, 0U)
		}

		val gui = CharacterStatsGui(FablesCharacters.instance, minimums, "#${target.id} ${target.name}'s stats",
				initialSliderValues)

		plugin.launch {
			val result = gui.execute(sender)
			denizenRun("characters_set_new_stats", mapOf(
					Pair("player", PlayerTag(target.player)),
					Pair("id", ElementTag(target.id.toLong())),
					Pair("strength", ElementTag(result.strength.toInt())),
					Pair("defense", ElementTag(result.defense.toInt())),
					Pair("agility", ElementTag(result.agility.toInt())),
					Pair("intelligence", ElementTag(result.intelligence.toInt()))
			))
		}
	}
}
