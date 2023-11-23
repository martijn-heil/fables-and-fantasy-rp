package com.fablesfantasyrp.plugin.lodestones.dal.h2

import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.database.getUuid
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.database.setUuid
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneData
import com.fablesfantasyrp.plugin.lodestones.dal.repository.LodestoneDataRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

class H2LodestoneDataRepository(private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, LodestoneData>(Int::class.java, dataSource), LodestoneDataRepository {

	override val TABLE_NAME = "FABLES_LODESTONES.LODESTONE"

	override fun create(v: LodestoneData): LodestoneData {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
				"(" +
				"location_x, " +
				"location_y, " +
				"location_z, " +
				"world, " +
				"name, " +
				"is_public" +
				") " +
				"VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
			stmnt.setInt(1, v.location.x)
			stmnt.setInt(2, v.location.y)
			stmnt.setInt(3, v.location.z)
			stmnt.setUuid(4, v.location.world)
			stmnt.setString(5, v.name)
			stmnt.setBoolean(6, v.isPublic)

			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			return LodestoneData(
					id = id,
					location = v.location,
					name = v.name,
			)
		}
	}

	override fun update(v: LodestoneData) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET " +
				"location_x = ?, " +
				"location_y = ?, " +
				"location_z = ?, " +
				"world = ?, " +
				"name = ?, " +
				"is_public = ? " +
				"WHERE id = ?")
			stmnt.setInt(1, v.location.x)
			stmnt.setInt(2, v.location.y)
			stmnt.setInt(3, v.location.z)
			stmnt.setUuid(4, v.location.world)
			stmnt.setString(5, v.name)
			stmnt.setBoolean(6, v.isPublic)

			stmnt.setInt(7, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun forId(id: Int): LodestoneData? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun forName(name: String): LodestoneData? {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE name = ?").apply {
				setString(1, name)
			}.executeQuery().asSequence().firstOrNull()?.let { fromRow(it) }
		}
	}

	override fun allNames(): Set<String> {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT name FROM $TABLE_NAME")
				.executeQuery()
				.asSequence()
				.map { it.getString("name") }
				.toSet()
		}
	}

	override fun fromRow(row: ResultSet): LodestoneData {
		val id = row.getInt("id")
		val isPublic = row.getBoolean("is_public")
		val locationX = row.getInt("location_x")
		val locationY = row.getInt("location_y")
		val locationZ = row.getInt("location_z")
		val world = row.getUuid("world")!!
		val locationName = row.getString("name")
		val location = BlockIdentifier(world, locationX, locationY, locationZ)

		return LodestoneData(
			id = id,
			location = location,
			name = locationName,
			isPublic = isPublic,
		)
	}
}
