package com.fablesfantasyrp.plugin.wardrobe.data.persistent

import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.wardrobe.data.Skin
import com.fablesfantasyrp.plugin.wardrobe.data.SkinRepository
import org.bukkit.Server
import java.sql.ResultSet
import javax.sql.DataSource

class H2SkinRepository(private val server: Server,
					   private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, Skin>(Int::class.java, dataSource), SkinRepository {

	override val TABLE_NAME = "FABLES_WARDROBE.SKIN"

	override fun create(v: Skin): Skin {
		dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("MERGE INTO $TABLE_NAME " +
					"(" +
					"textures_value, " +
					"textures_signature" +
					") " +
					"KEY(textures_value) " +
					"VALUES (?, ?)")
			stmnt.setString(1, v.value)
			stmnt.setString(2, v.signature)
			stmnt.executeUpdate()

			return forValue(v.value)
		}
	}

	override fun update(v: Skin) {
		throw UnsupportedOperationException()
	}

	override fun forId(id: Int): Skin? {
		return dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE id = ?")
			stmnt.setInt(1, id)
			val result = stmnt.executeQuery()
			if (!result.next()) return null
			fromRow(result)
		}
	}

	override fun forValue(value: String): Skin {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE " +
				"textures_value = ?").apply {
				this.setString(1, value)
			}.executeQuery().asSequence().first().let { fromRow(it) }
		}
	}

	override fun fromRow(row: ResultSet): Skin {
		val id = row.getInt("id")
		val value = row.getString("textures_value")
		val signature = row.getString("textures_signature")

		return Skin(
			id = id,
			value = value,
			signature = signature,
		)
	}
}
