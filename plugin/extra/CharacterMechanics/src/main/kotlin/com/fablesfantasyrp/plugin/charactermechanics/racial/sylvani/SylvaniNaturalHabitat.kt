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
package com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani

import com.fablesfantasyrp.plugin.charactermechanics.racial.base.BaseRaceBehavior
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.domain.EDEN
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.plugin.Plugin

// Players with this trait get two extra points in the strength stat.
class SylvaniNaturalHabitat(plugin: Plugin,
							characters: CharacterRepository,
							profileManager: ProfileManager,
							private val regionContainer: RegionContainer)
	: BaseRaceBehavior(Race.SYLVANI, plugin, characters, profileManager), StatsModifier {
	private val naturalHabitat = ArrayList<ProtectedRegion>()

	override fun init() {
		super.init()
		val wEden = BukkitAdapter.adapt(EDEN!!)
		val regionManager = regionContainer.get(wEden)!!
		naturalHabitat.add(regionManager.getRegion("77")!!)
		naturalHabitat.add(regionManager.getRegion("78")!!)
		naturalHabitat.add(regionManager.getRegion("79")!!)
		naturalHabitat.add(regionManager.getRegion("80")!!)
	}

	override fun calculateModifiers(who: Character): CharacterStatsModifier
		= if (who.race == race && !isInNaturalHabitat(who)) {
			CharacterStatsModifier(
				strength = -2,
				defense = -2,
				agility = -2,
				intelligence = -2
			)
		} else CharacterStatsModifier()

	private fun isInNaturalHabitat(who: Character): Boolean {
		val location = who.profile.location
		if (location.world != EDEN) return false
		val blockVector = BlockVector3.at(location.x, location.y, location.z)
		return naturalHabitat.any { it.contains(blockVector) }
	}
}
