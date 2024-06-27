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
package com.fablesfantasyrp.plugin.utils

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.koin.core.context.GlobalContext
import kotlin.reflect.KClass

object Services {
	inline fun<reified T: Any> get()
		= (Bukkit.getServicesManager().getRegistration(T::class.java)?.provider
			?: GlobalContext.getOrNull()?.getOrNull<T>())!!

	inline fun<reified T: Any> getMaybe()
		= Bukkit.getServicesManager().getRegistration(T::class.java)?.provider
			?: GlobalContext.getOrNull()?.getOrNull<T>()

	inline fun<reified T: Any> register(clazz: KClass<T>, instance: T, plugin: Plugin, priority: ServicePriority
		= ServicePriority.Normal) = Bukkit.getServicesManager().register(clazz.java, instance, plugin, priority)
}
