package com.fablesfantasyrp.plugin.basicsystem.data.persistent

import com.fablesfantasyrp.plugin.basicsystem.data.OffsetBlock
import com.fablesfantasyrp.plugin.basicsystem.data.entity.SlidingDoor
import com.fablesfantasyrp.plugin.basicsystem.data.entity.SlidingDoorRepository
import com.fablesfantasyrp.plugin.database.getList
import com.fablesfantasyrp.plugin.database.getUuid
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.setCollection
import com.fablesfantasyrp.plugin.database.setUuid
import com.fablesfantasyrp.plugin.utils.BlockLocation
import org.bukkit.Server
import org.h2.api.H2Type
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

class H2SlidingDoorRepository(private val server: Server,
							  private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, SlidingDoor>(Int::class.java, dataSource), SlidingDoorRepository, HasDirtyMarker<SlidingDoor> {

	override val TABLE_NAME = "FABLES_BASICSYSTEM.DOOR"

	override fun create(v: SlidingDoor): SlidingDoor {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
					"(" +
					"handle_location_x, " +
					"handle_location_y, " +
					"handle_location_z, " +
					"world, " +
					"blocks" +
					") " +
					"VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
			stmnt.setInt(1, v.handleLocation.x)
			stmnt.setInt(2, v.handleLocation.y)
			stmnt.setInt(3, v.handleLocation.z)
			stmnt.setUuid(4, v.world.uid)
			stmnt.setCollection(5, H2Type.JAVA_OBJECT, v.blocks)
			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			return SlidingDoor(
					id = id,
					handleLocation = v.handleLocation,
					blocks = v.blocks,
					world = v.world,
					dirtyMarker = dirtyMarker
			)
		}
	}

	override fun update(v: SlidingDoor) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET" +
					"handle_location_x = ?, " +
					"handle_location_y = ?, " +
					"handle_location_z = ?, " +
					"world = ?, " +
					"blocks = ?" +
					"WHERE id = ?")
			stmnt.setInt(1, v.handleLocation.x)
			stmnt.setInt(2, v.handleLocation.y)
			stmnt.setInt(3, v.handleLocation.z)
			stmnt.setUuid(4, v.world.uid)
			stmnt.setCollection(5, H2Type.JAVA_OBJECT, v.blocks)
			stmnt.setObject(6, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun forId(id: Int): SlidingDoor? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun fromRow(row: ResultSet): SlidingDoor {
		val id = row.getInt("id")
		val handleLocationX = row.getInt("handle_location_x")
		val handleLocationY = row.getInt("handle_location_y")
		val handleLocationZ = row.getInt("handle_location_z")
		val world = server.getWorld(row.getUuid("world")!!)!! // "Life without risks is not worth living." – Charles A. Lindbergh
		val blocks = row.getList<OffsetBlock>("blocks")
		val handleLocation = BlockLocation(handleLocationX, handleLocationY, handleLocationZ)

		return SlidingDoor(
				id = id,
				handleLocation = handleLocation,
				world = world,
				blocks = blocks
		)
	}
}
