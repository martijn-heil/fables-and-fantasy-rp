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

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import kotlinx.coroutines.*
import org.bukkit.plugin.Plugin
import java.lang.Long.max
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun every(plugin: Plugin, interval: Duration, what: suspend CoroutineScope.() -> Unit): Job {
	return plugin.launch {
		val scope = this
		while (true) {
			val duration = measureTime { what(scope) }
			delay(max((interval - duration).absoluteValue.inWholeMilliseconds, 1L))
		}
	}
}

fun<T> prunBlocking(plugin: Plugin, what: suspend CoroutineScope.() -> T)
	= runBlocking(block = what)

fun plaunch(plugin: Plugin, what: suspend CoroutineScope.() -> Unit)
	= plugin.launch(block = what)
