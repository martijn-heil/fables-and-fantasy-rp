package com.fablesfantasyrp.plugin.utils

import java.time.Duration
import java.time.Instant

class RateLimiter<T>(val interval: Duration) {
	val map = HashMap<T, Instant>()

	fun rateLimit(what: T): Boolean {
		val now = Instant.now()
		val last = map[what]

		return if (last == null) {
			map[what] = now
			false
		} else {
			val shouldRateLimit = Duration.between(last, now) <= interval
			if (!shouldRateLimit) map.remove(what)
			shouldRateLimit
		}
	}

	fun tick() {
		val now = Instant.now()
		map
			.filter { Duration.between(it.value, now) > interval }
			.forEach { map.remove(it.key) }
	}
}
