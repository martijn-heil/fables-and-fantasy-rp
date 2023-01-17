package com.fablesfantasyrp.plugin.staffplayerinstances

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

interface WorldBoundPlayerInstancesHook {
	fun allowToFlatroom(playerInstance: PlayerInstance)
}
