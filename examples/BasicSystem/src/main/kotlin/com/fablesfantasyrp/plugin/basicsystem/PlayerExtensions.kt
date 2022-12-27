package com.fablesfantasyrp.plugin.basicsystem

import com.fablesfantasyrp.plugin.basicsystem.data.entity.BasicSystemPlayer
import org.bukkit.entity.Player

val Player.basicSystem: BasicSystemPlayer get() = PLUGIN.players.forPlayer(this)
