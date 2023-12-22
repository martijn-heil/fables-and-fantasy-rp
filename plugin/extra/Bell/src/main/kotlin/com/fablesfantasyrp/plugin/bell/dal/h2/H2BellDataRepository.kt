package com.fablesfantasyrp.plugin.bell.dal.h2

import com.fablesfantasyrp.plugin.bell.dal.model.BellData
import com.fablesfantasyrp.plugin.bell.dal.repository.BellDataRepository
import com.fablesfantasyrp.plugin.database.*
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import dev.kord.common.entity.Snowflake
import org.h2.api.H2Type
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

class H2BellDataRepository(private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, BellData>(Int::class.java, dataSource), BellDataRepository {

	override val TABLE_NAME = "FABLES_BELL.BELL"

	override fun create(v: BellData): BellData {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
				"(" +
				"location_x, " +
				"location_y, " +
				"location_z, " +
				"world, " +
				"location_name, " +
				"discord_channel_id, " +
				"discord_role_ids" +
				") " +
				"VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
			stmnt.setInt(1, v.location.x)
			stmnt.setInt(2, v.location.y)
			stmnt.setInt(3, v.location.z)
			stmnt.setUuid(4, v.location.world)
			stmnt.setString(5, v.name)

			stmnt.setString(6, v.discordChannelId.toString())
			stmnt.setCollection(7, H2Type.VARCHAR, v.discordRoleIds.map { it.toString() })
			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			return BellData(
					id = id,
					location = v.location,
					name = v.name,
					discordChannelId = v.discordChannelId,
					discordRoleIds = v.discordRoleIds,
			)
		}
	}

	override fun update(v: BellData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET" +
				"location_x = ?, " +
				"location_y = ?, " +
				"location_z = ?, " +
				"world = ?, " +
				"location_name = ?, " +
				"discord_channel_id = ?, " +
				"discord_role_ids = ? " +
				"WHERE id = ?")
			stmnt.setInt(1, v.location.x)
			stmnt.setInt(2, v.location.y)
			stmnt.setInt(3, v.location.z)
			stmnt.setUuid(4, v.location.world)
			stmnt.setString(5, v.name)

			stmnt.setString(6, v.discordChannelId.toString())
			stmnt.setCollection(7, H2Type.VARCHAR, v.discordRoleIds.map { it.toString() })
			stmnt.setObject(8, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun createOrUpdate(v: BellData): BellData {
		throw NotImplementedError()
	}

	override fun forId(id: Int): BellData? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun forName(name: String): BellData? {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE location_name = ?").apply {
				setString(1, name)
			}.executeQuery().asSequence().firstOrNull()?.let { fromRow(it) }
		}
	}

	override fun allNames(): Set<String> {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT location_name FROM $TABLE_NAME")
				.executeQuery()
				.asSequence()
				.map { it.getString("location_name") }
				.toSet()
		}
	}

	override fun fromRow(row: ResultSet): BellData {
		val id = row.getInt("id")
		val locationX = row.getInt("location_x")
		val locationY = row.getInt("location_y")
		val locationZ = row.getInt("location_z")
		val world = row.getUuid("world")!!
		val locationName = row.getString("location_name")
		val discordChannelId = Snowflake(row.getString("discord_channel_id"))
		val discordRoleIds = row.getList<String>("discord_role_ids").map { Snowflake(it) }.toSet()
		val location = BlockIdentifier(world, locationX, locationY, locationZ)

		return BellData(
				id = id,
				location = location,
				name = locationName,
				discordChannelId = discordChannelId,
				discordRoleIds = discordRoleIds
		)
	}
}
