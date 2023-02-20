package com.fablesfantasyrp.plugin.tools.command.provider

import com.fablesfantasyrp.plugin.tools.MovementType
import com.fablesfantasyrp.plugin.tools.PlayerWeather
import com.fablesfantasyrp.plugin.tools.Weather
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.provider.EnumProvider
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Server

class ToolsModule(private val server: Server) : AbstractModule() {
	override fun configure() {
		bind(MinecraftTime::class.java).toProvider(MinecraftTimeProvider())
		bind(Weather::class.java).toProvider(EnumProvider(Weather::class.java))
		bind(PlayerWeather::class.java).toProvider(EnumProvider(PlayerWeather::class.java))
		bind(GameMode::class.java).toProvider(EnumProvider(GameMode::class.java))
		bind(MovementType::class.java).toProvider(EnumProvider(MovementType::class.java))
		bind(Location::class.java).toProvider(LocationProvider(server))
	}
}
