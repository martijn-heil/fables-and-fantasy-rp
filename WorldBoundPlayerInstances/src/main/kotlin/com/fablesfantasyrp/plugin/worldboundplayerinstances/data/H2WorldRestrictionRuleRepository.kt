package com.fablesfantasyrp.plugin.worldboundplayerinstances.data

import com.fablesfantasyrp.plugin.database.getUuid
import com.fablesfantasyrp.plugin.database.setUuid
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Player
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class H2WorldRestrictionRuleRepository(private val server: Server,
									   private val dataSource: DataSource,
									   private val playerInstanceRepository: PlayerInstanceRepository)
	: WorldRestrictionRuleRepository {
	private val TABLE_NAME = "FABLES_WORLDBOUNDPLAYERINSTANCES.RULES"

	override fun create(v: WorldRestrictionRule): WorldRestrictionRule {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME (player_instance, world, action) VALUES (?, ?, ?)")
			stmnt.setInt(1, v.id.first.id)
			stmnt.setUuid(2, v.id.second)
			stmnt.setString(3, v.action.name)
			stmnt.executeUpdate()
			v
		}
	}

	override fun updateOrCreate(v: WorldRestrictionRule): WorldRestrictionRule {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement(
					"MERGE INTO $TABLE_NAME (player_instance, world, action) " +
					"KEY (player_instance, world) " +
					"VALUES (?, ?, ?)")
			stmnt.setInt(1, v.id.first.id)
			stmnt.setUuid(2, v.id.second)
			stmnt.setString(3, v.action.name)
			stmnt.executeUpdate()
			v
		}
	}

	override fun update(v: WorldRestrictionRule) {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME (player_instance, world, action)")
			stmnt.setInt(1, v.id.first.id)
			stmnt.setUuid(2, v.id.second)
			stmnt.setString(3, v.action.name)
			stmnt.executeUpdate()
		}
	}

	override fun all(): Collection<WorldRestrictionRule> {
		return dataSource.connection.use { connection ->
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

	override fun destroy(v: WorldRestrictionRule) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE id = ?")
			stmnt.setObject(1, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun getBoundWorlds(playerInstance: PlayerInstance): Collection<World> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT world FROM $TABLE_NAME WHERE player_instance = ? AND action = 'BOUND'")
			stmnt.setInt(1, playerInstance.id)
			val result = stmnt.executeQuery()
			val worlds = ArrayList<World>()
			while (result.next()) server.getWorld(result.getUuid("world"))?.let { worlds.add(it) }
			worlds
		}
	}

	override fun getExplicitlyAllowedPlayerInstances(world: World): Collection<PlayerInstance> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT player_instance FROM $TABLE_NAME WHERE world = ? AND action IN ('BOUND', 'ALLOWED')")
			stmnt.setUuid(1, world.uid)
			val result = stmnt.executeQuery()
			val instances = ArrayList<PlayerInstance>()
			while (result.next()) instances.add(playerInstanceRepository.forId(result.getInt("player_instance"))!!)
			instances
		}
	}

	override fun getExplicitlyAllowedPlayerInstances(world: World, player: Player): Collection<PlayerInstance> {
		val ownedInstances = playerInstanceRepository.forOwner(player).map { it.id }.toHashSet()

		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT player_instance FROM $TABLE_NAME WHERE world = ? AND action IN ('BOUND', 'ALLOWED')")
			stmnt.setUuid(1, world.uid)
			val result = stmnt.executeQuery()
			val instances = ArrayList<PlayerInstance>()
			while (result.next()) {
				val playerInstanceId = result.getInt("player_instance")
				if (!ownedInstances.contains(playerInstanceId)) continue
				val playerInstance = playerInstanceRepository.forId(playerInstanceId)!!
				instances.add(playerInstance)
			}
			instances
		}
	}

	override fun forId(id: Pair<PlayerInstance, UUID>): WorldRestrictionRule? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE player_instance = ? AND world = ?")
			stmnt.setInt(1, id.first.id)
			stmnt.setUuid(2, id.second)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun allIds(): Collection<Pair<PlayerInstance, UUID>> {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT player_instance, world FROM $TABLE_NAME")
			val result = stmnt.executeQuery()
			val all = ArrayList<Pair<PlayerInstance, UUID>>()
			while (result.next()) {
				val playerInstance = playerInstanceRepository.forId(result.getInt("player_instance"))!!
				val worldUuid = result.getObject("world", UUID::class.java)
				all.add(Pair(playerInstance, worldUuid))
			}
			all
		}
	}

	private fun fromRow(row: ResultSet): WorldRestrictionRule {
		val playerInstance = playerInstanceRepository.forId(row.getInt("player_instance"))!!
		val worldUuid = row.getObject("world", UUID::class.java)

		return WorldRestrictionRule(
				id = Pair(playerInstance, worldUuid),
				action = WorldRestrictionRuleAction.valueOf(row.getString("action"))
		)
	}
}
