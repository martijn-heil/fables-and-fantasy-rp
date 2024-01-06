package com.fablesfantasyrp.plugin.database

import com.fablesfantasyrp.plugin.utils.every
import com.github.shynixn.mccoroutine.bukkit.launch
import com.google.common.base.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.bukkit.plugin.Plugin
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Schedules a randomly staggered repeating task intended for saving data to disk.
 * By randomly staggering these tasks, there is less chance of multiple disk writes having to occur
 * straight after each other, smoothing out the overall performance impact.
 */
fun scheduleRepeatingDataSave(plugin: Plugin, block: suspend CoroutineScope.() -> Unit) {
	plugin.launch {
		delay(Random.nextInt(0, 10*60).seconds)
		every(plugin, 10.minutes, block)
	}
}

fun<T, R> T.warnBlockingIO(plugin: Plugin, block: T.() -> R): R {
	val server = plugin.server
	val watch = Stopwatch.createStarted()
	val result = block()
	watch.stop()

	if (server.isPrimaryThread) {
		val stackTrace = Thread.currentThread().stackTrace
		val at = "${stackTrace[2].className}:${stackTrace[2].lineNumber}"
		plugin.logger.warning("[tick ${server.currentTick}] Blocking I/O on main server thread took ${watch.elapsed().toMillis()}ms at $at")
	}

	return result
}
