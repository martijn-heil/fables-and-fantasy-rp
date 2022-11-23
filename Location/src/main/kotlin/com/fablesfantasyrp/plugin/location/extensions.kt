package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

var PlayerInstance.location
	get() = PLUGIN.playerInstanceLocationRepository.forOwner(this).location
	set(value) { PLUGIN.playerInstanceLocationRepository.forOwner(this).location = value }
