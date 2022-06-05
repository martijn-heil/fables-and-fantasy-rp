@file:OptIn(ExperimentalTime::class)

package com.fablesfantasyrp.plugin.utils

import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import java.lang.Long.max
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

interface EveryScope {
	fun stop()
}

fun every(plugin: SuspendingJavaPlugin, interval: Duration, what: suspend CoroutineScope.() -> Unit) {
	plugin.launch {
		val scope = this
		while (true) {
			val duration = measureTime { what(scope) }
			delay(max((interval - duration).absoluteValue.inWholeMilliseconds, 1L))
		}
	}
}
