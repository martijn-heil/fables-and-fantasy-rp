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
