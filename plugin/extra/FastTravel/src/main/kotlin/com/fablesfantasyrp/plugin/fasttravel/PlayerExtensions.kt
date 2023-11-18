package com.fablesfantasyrp.plugin.fasttravel

import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelPlayer
import org.bukkit.entity.Player

val Player.fastTravel: FastTravelPlayer get() = PLUGIN.players.forPlayer(this)
