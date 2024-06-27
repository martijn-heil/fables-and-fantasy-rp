/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.tools.command.provider

import com.fablesfantasyrp.plugin.tools.MovementType
import com.fablesfantasyrp.plugin.tools.PlayerWeather
import com.fablesfantasyrp.plugin.tools.Weather
import com.fablesfantasyrp.caturix.parametric.AbstractModule
import com.fablesfantasyrp.caturix.parametric.provider.EnumProvider
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
