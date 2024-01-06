package com.fablesfantasyrp.plugin.lodestones.dal.h2

import com.fablesfantasyrp.plugin.database.getUuid
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.database.setUuid
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneBannerData
import com.fablesfantasyrp.plugin.lodestones.dal.repository.LodestoneBannerDataRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

class H2LodestoneBannerDataRepository(private val plugin: Plugin,
									  private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, LodestoneBannerData>(Int::class.java, plugin, dataSource), LodestoneBannerDataRepository {

	override val TABLE_NAME = "FABLES_LODESTONES.LODESTONE_BANNER"

	override fun create(v: LodestoneBannerData): LodestoneBannerData = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("INSERT INTO $TABLE_NAME " +
				"(" +
				"location_x, " +
				"location_y, " +
				"location_z, " +
				"world, " +
				"lodestone_id" +
				") " +
				"VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
			stmnt.setInt(1, v.location.x)
			stmnt.setInt(2, v.location.y)
			stmnt.setInt(3, v.location.z)
			stmnt.setUuid(4, v.location.world)
			stmnt.setInt(5, v.lodestoneId)

			stmnt.executeUpdate()
			val rs = stmnt.generatedKeys
			rs.next()
			val id = rs.getInt(1)
			LodestoneBannerData(
					id = id,
					location = v.location,
					lodestoneId = v.lodestoneId
			)
		}
	}

	override fun update(v: LodestoneBannerData): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("UPDATE $TABLE_NAME SET" +
				"location_x = ?, " +
				"location_y = ?, " +
				"location_z = ?, " +
				"world = ?, " +
				"lodestone_id = ?, " +
				"WHERE id = ?")
			stmnt.setInt(1, v.location.x)
			stmnt.setInt(2, v.location.y)
			stmnt.setInt(3, v.location.z)
			stmnt.setUuid(4, v.location.world)
			stmnt.setInt(5, v.lodestoneId)

			stmnt.setInt(6, v.id)
			stmnt.executeUpdate()
		}
	}

	override fun createOrUpdate(v: LodestoneBannerData): LodestoneBannerData {
		throw NotImplementedError()
	}

	override fun fromRow(row: ResultSet): LodestoneBannerData {
		val id = row.getInt("id")
		val locationX = row.getInt("location_x")
		val locationY = row.getInt("location_y")
		val locationZ = row.getInt("location_z")
		val world = row.getUuid("world")!!
		val location = BlockIdentifier(world, locationX, locationY, locationZ)
		val lodestoneId = row.getInt("lodestone_id")

		return LodestoneBannerData(
			id = id,
			location = location,
			lodestoneId = lodestoneId
		)
	}
}
