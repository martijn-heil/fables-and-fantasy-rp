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
package com.fablesfantasyrp.plugin.locks.data.persistent

import com.denizenscript.denizen.objects.LocationTag
import com.denizenscript.denizen.objects.PlayerTag
import com.denizenscript.denizencore.objects.core.ElementTag
import com.denizenscript.denizencore.objects.core.ListTag
import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.denizeninterop.denizenParseTag
import com.fablesfantasyrp.plugin.denizeninterop.denizenRun
import com.fablesfantasyrp.plugin.locks.data.LockRole
import com.fablesfantasyrp.plugin.locks.data.SimpleLockData
import com.fablesfantasyrp.plugin.locks.data.SimpleLockDataRepository
import org.bukkit.Location
import org.bukkit.OfflinePlayer

private fun SimpleLockData.asDenizen(): MapTag {
	val map = MapTag()
	val users = ListTag()
	for (user in this.members.filter { it.value == LockRole.USER }) {
		users.addObject(PlayerTag.mirrorBukkitPlayer(user.key))
	}

	val moderators = ListTag()
	for (moderator in this.members.filter { it.value == LockRole.USER }) {
		moderators.addObject(PlayerTag.mirrorBukkitPlayer(moderator.key))
	}

	map.putObject("mods", moderators)
	map.putObject("users", users)
	map.putObject("owner", PlayerTag.mirrorBukkitPlayer(this.owner))

	return map
}

private fun Location.denizenNormalize(): Location {
	return denizenParseTag("<proc[location_normalize].context[<[location]>]>",
			mapOf(Pair("location", LocationTag(this)))) as LocationTag
}

private fun SimpleLockData.Companion.fromDenizen(map: MapTag, location: Location): SimpleLockData {
	val users = map.getObject("users") as ListTag
	val moderators = map.getObject("mods") as ListTag
	val owner = (map.getObject("owner") as PlayerTag).offlinePlayer

	val members = HashMap<OfflinePlayer, LockRole>()
	members.putAll(users.objectForms.map { Pair((it as PlayerTag).offlinePlayer, LockRole.USER) })
	members.putAll(moderators.objectForms.map { Pair((it as PlayerTag).offlinePlayer, LockRole.MODERATOR) })

	return SimpleLockData(owner, members, location)
}

class DenizenSimpleLockDataRepository : SimpleLockDataRepository {
	override fun destroy(v: SimpleLockData) {
		denizenRun("location_unset", mapOf(
				Pair("location", LocationTag(v.location)),
				Pair("key", ElementTag("locks"))
		))
	}

	override fun create(v: SimpleLockData) = createOrUpdate(v)
	override fun update(v: SimpleLockData) { createOrUpdate(v) }

	override fun createOrUpdate(v: SimpleLockData): SimpleLockData {
		denizenRun("location_set", mapOf(
			Pair("location", LocationTag(v.location)),
			Pair("key", ElementTag("locks")),
			Pair("value", v.asDenizen())
		))
		return v
	}

	override fun forId(id: Location): SimpleLockData? {
		val map = denizenParseTag("<proc[location_get_key].context[<[location]>|locks]>",
				mapOf(Pair("location", LocationTag(id)))) as? MapTag ?: return null
		return SimpleLockData.fromDenizen(map, id.denizenNormalize())
	}

	override fun all(): Collection<SimpleLockData> {
		throw NotImplementedError()
	}

	override fun allIds(): Collection<Location> {
		throw NotImplementedError()
	}
}
