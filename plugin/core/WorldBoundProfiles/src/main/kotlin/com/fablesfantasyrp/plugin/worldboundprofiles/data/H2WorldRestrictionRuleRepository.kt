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

import com.fablesfantasyrp.plugin.database.getUuid
import com.fablesfantasyrp.plugin.database.setUuid
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class H2WorldRestrictionRuleRepository(private val plugin: Plugin,
									   private val dataSource: DataSource,
									   private val profileRepository: ProfileRepository)
	: WorldRestrictionRuleRepository {
	private val TABLE_NAME = "FABLES_WORLDBOUNDPROFILES.RULE"
	private val server = plugin.server

	override fun create(v: WorldRestrictionRule): WorldRestrictionRule = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (profile, world, action) VALUES (?, ?, ?)")
			stmnt.setInt(1, v.id.first.id)
			stmnt.setUuid(2, v.id.second)
			stmnt.setString(3, v.action.name)
			stmnt.executeUpdate()
			v
		}
	}

	override fun updateOrCreate(v: WorldRestrictionRule): WorldRestrictionRule = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement(
					"MERGE INTO $TABLE_NAME (profile, world, action) " +
					"KEY (profile, world) " +
					"VALUES (?, ?, ?)")
			stmnt.setInt(1, v.id.first.id)
			stmnt.setUuid(2, v.id.second)
			stmnt.setString(3, v.action.name)
			stmnt.executeUpdate()
			v
		}
	}

	override fun update(v: WorldRestrictionRule): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME (profile, world, action)")
			stmnt.setInt(1, v.id.first.id)
			stmnt.setUuid(2, v.id.second)
			stmnt.setString(3, v.action.name)
			stmnt.executeUpdate()
		}
	}

	override fun createOrUpdate(v: WorldRestrictionRule): WorldRestrictionRule {
		throw NotImplementedError()
	}

	override fun all(): Collection<WorldRestrictionRule> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<WorldRestrictionRule>()
			while (result.next()) {
				val parsed = fromRow(result)
				all.add(parsed)
			}
			all
		}
	}

	override fun destroy(v: WorldRestrictionRule): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun getBoundWorlds(profile: Profile): Collection<World> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT world FROM $TABLE_NAME WHERE profile = ? AND action = 'BOUND'")
			stmnt.setInt(1, profile.id)
			val result = stmnt.executeQuery()
			val worlds = ArrayList<World>()
			while (result.next()) server.getWorld(result.getUuid("world")!!)?.let { worlds.add(it) }
			worlds
		}
	}

	override fun getBoundWorlds(profiles: Collection<Profile>): Map<Profile, Set<World>> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT profile, world FROM $TABLE_NAME WHERE profile " +
					"IN (${profiles.map { it.id }.joinToString(", ")}) AND action = 'BOUND'")
			val result = stmnt.executeQuery()
			val worlds = HashMap<Profile, HashSet<World>>()
			while (result.next()) {
				val profile = profileRepository.forId(result.getInt("profile"))!!
				val world = server.getWorld(result.getUuid("world")!!) ?: continue
				worlds.putIfAbsent(profile, HashSet())
				worlds[profile]!!.add(world)
			}
			worlds
		}
	}

	override fun forProfiles(profiles: Collection<Profile>): Map<Profile, Collection<WorldRestrictionRule>> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile " +
					"IN (${profiles.map { it.id }.joinToString(", ")})")
			val result = stmnt.executeQuery()
			val worlds = HashMap<Profile, MutableCollection<WorldRestrictionRule>>()
			while (result.next()) {
				val row = fromRow(result)
				worlds.putIfAbsent(row.id.first, HashSet())
				worlds[row.id.first]!!.add(row)
			}
			worlds
		}
	}

	override fun getExplicitlyAllowedProfiles(world: World): Collection<Profile> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT DISTINCT profile FROM $TABLE_NAME WHERE world = ? AND action IN ('BOUND', 'ALLOWED')")
			stmnt.setUuid(1, world.uid)
			val result = stmnt.executeQuery()
			val profiles = ArrayList<Profile>()
			while (result.next()) profiles.add(profileRepository.forId(result.getInt("profile"))!!)
			profiles
		}
	}

	override fun getExplicitlyAllowedProfiles(world: World, player: Player): Collection<Profile> {
		val ownedProfiles = profileRepository.activeForOwner(player).map { it.id }.toHashSet()

		return warnBlockingIO(plugin) {
			dataSource.connection.use { connection ->
				val stmnt = connection.prepareStatement("SELECT DISTINCT profile FROM $TABLE_NAME WHERE world = ? AND action IN ('BOUND', 'ALLOWED')")
				stmnt.setUuid(1, world.uid)
				val result = stmnt.executeQuery()
				val profiles = ArrayList<Profile>()
				while (result.next()) {
					val profileId = result.getInt("profile")
					if (!ownedProfiles.contains(profileId)) continue
					val profile = profileRepository.forId(profileId)!!
					profiles.add(profile)
				}
				profiles
			}
		}
	}

	override fun forId(id: Pair<Profile, UUID>): WorldRestrictionRule? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile = ? AND world = ?")
			stmnt.setInt(1, id.first.id)
			stmnt.setUuid(2, id.second)
			val result = stmnt.executeQuery()
			if (!result.next()) return@use null
			fromRow(result)
		}
	}

	override fun allIds(): Collection<Pair<Profile, UUID>> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT profile, world FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Pair<Profile, UUID>>()
			while (result.next()) {
				val profile = profileRepository.forId(result.getInt("profile"))!!
				val worldUuid = result.getObject("world", UUID::class.java)
				all.add(Pair(profile, worldUuid))
			}
			all
		}
	}

	private fun fromRow(row: ResultSet): WorldRestrictionRule {
		val profile = profileRepository.forId(row.getInt("profile"))!!
		val worldUuid = row.getObject("world", UUID::class.java)

		return WorldRestrictionRule(
				id = Pair(profile, worldUuid),
				action = WorldRestrictionRuleAction.valueOf(row.getString("action"))
		)
	}
}
