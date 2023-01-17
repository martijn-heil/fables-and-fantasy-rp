package com.fablesfantasyrp.plugin.staffplayerinstances.data

import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

interface StaffPlayerInstanceRepository : MutableRepository<PlayerInstance> {
	override fun contains(v: PlayerInstance): Boolean
}
