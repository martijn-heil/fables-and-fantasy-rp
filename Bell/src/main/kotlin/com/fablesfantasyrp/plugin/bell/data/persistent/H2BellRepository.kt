package com.fablesfantasyrp.plugin.bell.data.persistent

import com.fablesfantasyrp.plugin.bell.data.entity.Bell
import com.fablesfantasyrp.plugin.bell.data.entity.BellRepository
import com.fablesfantasyrp.plugin.database.*
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import dev.kord.common.entity.Snowflake
import org.h2.api.H2Type
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

class H2BellRepository(private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, Bell>(Int::class.java, dataSource), BellRepository, HasDirtyMarker<Bell> {

	override val TABLE_NAME = "FABLES_BELL.BELL"

	override fun create(v: Bell): Bell {
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
			stmnt.setString(5, v.locationName)

			stmnt.setString(6, v.discordChannelId.toString())
			stmnt.setCollection(7, H2Type.VARCHAR, v.discordRoleIds.map { it.toString() })
			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			return Bell(
					id = id,
					location = v.location,
					locationName = v.locationName,
					discordChannelId = v.discordChannelId,
					discordRoleIds = v.discordRoleIds,
					dirtyMarker = dirtyMarker
			)
		}
	}

	override fun update(v: Bell) {
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
			stmnt.setString(5, v.locationName)

			stmnt.setString(6, v.discordChannelId.toString())
			stmnt.setCollection(7, H2Type.VARCHAR, v.discordRoleIds.map { it.toString() })
			stmnt.setObject(8, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun forId(id: Int): Bell? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun forName(name: String): Bell? {
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

	override fun fromRow(row: ResultSet): Bell {
		val id = row.getInt("id")
		val locationX = row.getInt("location_x")
		val locationY = row.getInt("location_y")
		val locationZ = row.getInt("location_z")
		val world = row.getUuid("world")!!
		val locationName = row.getString("location_name")
		val discordChannelId = Snowflake(row.getString("discord_channel_id"))
		val discordRoleIds = row.getList<String>("discord_role_ids").map { Snowflake(it) }.toSet()
		val location = BlockIdentifier(world, locationX, locationY, locationZ)

		return Bell(
				id = id,
				location = location,
				locationName = locationName,
				discordChannelId = discordChannelId,
				discordRoleIds = discordRoleIds
		)
	}

	override fun forLocation(location: BlockIdentifier): Bell? {
		throw NotImplementedError()
	}
}
