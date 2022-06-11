package com.fablesfantasyrp.plugin.characters.command

import com.denizenscript.denizen.objects.PlayerTag
import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.characters.*
import com.fablesfantasyrp.plugin.denizeninterop.denizenRun
import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.launch
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands(private val plugin: SuspendingJavaPlugin) {
	@Command(aliases = ["cardother"], desc = "Show another character's card")
	@Require(Permission.Command.Cardother)
	fun cardother(@Sender sender: Player, target: PlayerCharacter) {
		denizenRun("characters_print_card", mapOf(
				Pair("player", PlayerTag(sender)),
				Pair("target", PlayerTag(target.player)),
				Pair("id", ElementTag(target.id.toLong())),
		))
	}

	@Command(aliases = ["updatestats"], desc = "Update a player's character stats")
	@Require(Permission.Command.Updatestats)
	fun updatestats(@Sender sender: Player, target: PlayerCharacter) {
		val minimums = target.race.boosters + CharacterStats(2U, 2U, 2U, 2U)
		var initialSliderValues = target.stats - minimums
		if (initialSliderValues.strength > 8U ||
				initialSliderValues.defense > 8U ||
				initialSliderValues.agility > 8U ||
				initialSliderValues.intelligence > 8u) {
			sender.sendMessage("$SYSPREFIX Detected that you will be editing legacy player stats, starting with a clean slate.")
			initialSliderValues = CharacterStats(0U, 0U, 0U, 0U)
		}

		val gui = CharacterStatsGui(FablesCharacters.instance, minimums, "(#${target.id}) ${target.name}'s stats",
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
