package com.fablesfantasyrp.plugin.targeting

import com.denizenscript.denizen.objects.PlayerTag
import com.fablesfantasyrp.plugin.denizeninterop.ex
import org.bukkit.entity.Player

class DenizenGlowingManager : GlowingManager {
	override fun glowFor(glowing: Player, viewing: Player) {
		ex(mapOf(
				Pair("player", PlayerTag.mirrorBukkitPlayer(viewing)),
				Pair("target", PlayerTag.mirrorBukkitPlayer(glowing))
		),
				"adjust <queue> linked_player:<[player]>",
				"glow <[target]>")
	}

	override fun unglowFor(glowing: Player, viewing: Player) {
		ex(mapOf(
				Pair("player", PlayerTag.mirrorBukkitPlayer(viewing)),
				Pair("target", PlayerTag.mirrorBukkitPlayer(glowing))
		),
				"adjust <queue> linked_player:<[player]>",
				"glow <[target]> false")
	}
}
