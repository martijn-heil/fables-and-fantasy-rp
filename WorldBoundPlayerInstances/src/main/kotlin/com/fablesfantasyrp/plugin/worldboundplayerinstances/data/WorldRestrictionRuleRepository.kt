package com.fablesfantasyrp.plugin.worldboundplayerinstances.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

interface WorldRestrictionRuleRepository :
		KeyedRepository<Pair<PlayerInstance, UUID>, WorldRestrictionRule>,
		MutableRepository<WorldRestrictionRule> {
	fun getBoundWorlds(playerInstance: PlayerInstance): Collection<World>
	fun getBoundWorlds(playerInstances: Collection<PlayerInstance>): Map<PlayerInstance, Set<World>>
	fun getExplicitlyAllowedPlayerInstances(world: World): Collection<PlayerInstance>
	fun getExplicitlyAllowedPlayerInstances(world: World, player: Player): Collection<PlayerInstance>
	fun updateOrCreate(v: WorldRestrictionRule): WorldRestrictionRule
	fun forPlayerInstances(playerInstances: Collection<PlayerInstance>): Map<PlayerInstance, Collection<WorldRestrictionRule>>
}
