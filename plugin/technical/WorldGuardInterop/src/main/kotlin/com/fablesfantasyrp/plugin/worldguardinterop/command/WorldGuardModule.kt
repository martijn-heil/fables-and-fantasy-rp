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
package com.fablesfantasyrp.plugin.worldguardinterop.command

import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import com.fablesfantasyrp.plugin.worldguardinterop.command.provider.WorldGuardRegionProvider
import com.fablesfantasyrp.caturix.parametric.AbstractModule
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Server

class WorldGuardModule(private val server: Server, private val regionContainer: RegionContainer) : AbstractModule() {
	override fun configure() {
		bind(WorldGuardRegion::class.java).toProvider(WorldGuardRegionProvider(server, regionContainer))
	}
}
