package com.fablesfantasyrp.plugin.wardrobe.data.persistent

import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.database.repository.BaseH2KeyedRepository
import com.fablesfantasyrp.plugin.wardrobe.data.Skin
import com.fablesfantasyrp.plugin.wardrobe.data.SkinRepository
import org.bukkit.Server
import org.bukkit.profile.PlayerTextures.SkinModel
import java.net.URL
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class H2SkinRepository(private val server: Server,
					   private val dataSource: DataSource)
	: BaseH2KeyedRepository<Int, Skin>(Int::class.java, dataSource), SkinRepository {

	override val TABLE_NAME = "FABLES_WARDROBE.SKIN"

	override fun create(v: Skin): Skin {
		val profile = server.createProfile(UUID.randomUUID(), null)
		profile.setProperty(v.toProfileProperty())
		val skinUrl = profile.textures.skin!!
		val skinModel = profile.textures.skinModel

		dataSource.connection.use { connection ->
			connection.prepareStatement("MERGE INTO $TABLE_NAME " +
					"(" +
					"textures_value, " +
					"textures_signature, " +
					"textures_skin_url, " +
					"textures_skin_model" +
					") " +
					"KEY(textures_skin_url, textures_skin_model) " +
					"VALUES (?, ?, ?, ?)").apply {

				this.setString(1, v.value)
				this.setString(2, v.signature)
				this.setString(3, skinUrl.toString())
				this.setString(4, skinModel.name)
			}.executeUpdate()

			return forValue(skinUrl, skinModel)
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

	override fun forValue(skinUrl: URL, skinModel: SkinModel): Skin {
		return dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE " +
				"textures_skin_url = ? AND textures_skin_model = ?").apply {
				this.setString(1, skinUrl.toString())
				this.setString(2, skinModel.name)
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
