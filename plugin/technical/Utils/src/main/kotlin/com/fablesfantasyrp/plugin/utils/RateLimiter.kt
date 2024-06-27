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
