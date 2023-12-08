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
