package com.fablesfantasyrp.plugin.knockout.command

import com.fablesfantasyrp.plugin.knockout.Permission
import com.fablesfantasyrp.plugin.knockout.knockout
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class Commands {
	@Command(aliases = ["knockout", "ko"], desc = "Knock out a player")
	@Require(Permission.Command.Knockout)
	fun knockout(target: Player, @CommandTarget by: Player?) {
		val knockoutPlayer = target.knockout
		if (!knockoutPlayer.isKnockedOut) knockoutPlayer.knockout(EntityDamageEvent.DamageCause.CUSTOM, by)
	}

	@Command(aliases = ["revive"], desc = "Knock out a player")
	@Require(Permission.Command.Revive)
	fun revive(target: Player, @CommandTarget by: Player?) {
		val knockoutPlayer = target.knockout
		if (knockoutPlayer.isKnockedOut) knockoutPlayer.revive(by)
	}
}
