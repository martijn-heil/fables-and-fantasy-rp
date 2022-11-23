package com.fablesfantasyrp.plugin.playerinstance.command.provider

import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.sk89q.intake.parametric.AbstractModule

class PlayerInstanceModule(private val playerInstances: EntityPlayerInstanceRepository<*>) : AbstractModule() {
	override fun configure() {
		bind(PlayerInstance::class.java).toProvider(PlayerInstanceProvider(playerInstances))
	}
}
