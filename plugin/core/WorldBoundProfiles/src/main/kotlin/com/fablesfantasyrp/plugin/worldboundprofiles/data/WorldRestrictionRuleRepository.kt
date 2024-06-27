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
package com.fablesfantasyrp.plugin.worldboundprofiles.data

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

interface WorldRestrictionRuleRepository :
		KeyedRepository<Pair<Profile, UUID>, WorldRestrictionRule>,
	MutableRepository<WorldRestrictionRule> {
	fun getBoundWorlds(profile: Profile): Collection<World>
	fun getBoundWorlds(profiles: Collection<Profile>): Map<Profile, Set<World>>
	fun getExplicitlyAllowedProfiles(world: World): Collection<Profile>
	fun getExplicitlyAllowedProfiles(world: World, player: Player): Collection<Profile>
	fun updateOrCreate(v: WorldRestrictionRule): WorldRestrictionRule
	fun forProfiles(profiles: Collection<Profile>): Map<Profile, Collection<WorldRestrictionRule>>
}
