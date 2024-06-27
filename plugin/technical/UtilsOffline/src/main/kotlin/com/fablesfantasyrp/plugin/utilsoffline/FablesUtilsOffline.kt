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
package com.fablesfantasyrp.plugin.utilsoffline

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import net.quazar.offlinemanager.api.OfflineManagerAPI
import org.bukkit.plugin.java.JavaPlugin

lateinit var offlineManagerAPI: OfflineManagerAPI
	private set

class FablesUtilsOffline : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		offlineManagerAPI = server.pluginManager.getPlugin("OfflineManager") as OfflineManagerAPI
	}
}
