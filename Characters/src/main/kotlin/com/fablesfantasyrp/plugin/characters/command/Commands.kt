package com.fablesfantasyrp.plugin.characters.command

import com.fablesfantasyrp.plugin.characters.CharacterStats
import com.fablesfantasyrp.plugin.characters.CharacterStatsGui
import com.fablesfantasyrp.plugin.characters.FablesCharacters
import com.fablesfantasyrp.plugin.characters.PLUGIN
import com.github.shynixn.mccoroutine.launch
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["debug1"], desc = "test")
	@Require("fables.form.command.debug1")
	fun debug1(@Sender origin: Player) {
		val gui = CharacterStatsGui(FablesCharacters.instance, CharacterStats(
				strength = 2U,
				defense = 3U,
				agility = 2U,
				intelligence = 4U,
		))

		PLUGIN.launch {
			val result = gui.execute(origin)
			PLUGIN.logger.info(result.toString())
		}
	}
}
