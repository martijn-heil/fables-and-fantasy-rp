package com.fablesfantasyrp.plugin.worldguardinterop.command

import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.fablesfantasyrp.plugin.worldguardinterop.command.provider.WorldGuardRegionProvider
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Server

class WorldGuardModule(private val server: Server, private val regionContainer: RegionContainer) : AbstractModule() {
	override fun configure() {
		bind(WorldGuardRegion::class.java).toProvider(WorldGuardRegionProvider(server, regionContainer))
	}
}
