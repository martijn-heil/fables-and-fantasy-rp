package com.fablesfantasyrp.plugin.wardrobe.data.persistent

import com.fablesfantasyrp.plugin.database.asSequence
import com.fablesfantasyrp.plugin.database.warnBlockingIO
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.wardrobe.data.ProfileSkin
import com.fablesfantasyrp.plugin.wardrobe.data.ProfileSkinRepository
import com.fablesfantasyrp.plugin.wardrobe.data.SkinRepository
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import java.time.Instant
import javax.sql.DataSource

class H2ProfileSkinRepository(private val plugin: Plugin,
							  private val dataSource: DataSource,
							  private val skins: SkinRepository,
							  private val profiles: ProfileRepository) : ProfileSkinRepository {
	private val TABLE_NAME = "FABLES_WARDROBE.PROFILE_SKIN"

	override fun forProfile(profile: Profile): Collection<ProfileSkin> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile_id = ?").apply {
				this.setInt(1, profile.id)
			}.executeQuery().asSequence().map { fromRow(it) }.toList()
		}
	}

	override fun getLastUsed(profile: Profile): ProfileSkin? = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME WHERE profile_id = ? ORDER BY last_used_at DESC LIMIT 1").apply {
				this.setInt(1, profile.id)
			}.executeQuery().asSequence().firstOrNull()?.let { fromRow(it) }
		}
	}

	override fun destroy(v: ProfileSkin): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("DELETE FROM $TABLE_NAME WHERE profile_id = ? AND skin_id = ?").apply {
				this.setInt(1, v.profile.id)
				this.setInt(2, v.skin.id)
			}.executeUpdate()
		}
	}

	override fun create(v: ProfileSkin): ProfileSkin = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("INSERT INTO $TABLE_NAME " +
				"(profile_id, skin_id, description, last_used_at) VALUES" +
				"(?, ?, ?, ?)").apply {
				this.setInt(1, v.profile.id)
				this.setInt(2, v.skin.id)
				this.setString(3, v.description)
				this.setObject(4, v.lastUsedAt)
			}.executeUpdate()
		}

		v
	}

	override fun createOrUpdate(v: ProfileSkin): ProfileSkin = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("MERGE INTO $TABLE_NAME " +
				"(profile_id, skin_id, description, last_used_at) KEY(profile_id, skin_id) VALUES " +
				"(?, ?, ?, ?)").apply {
				this.setInt(1, v.profile.id)
				this.setInt(2, v.skin.id)
				this.setString(3, v.description)
				this.setObject(4, v.lastUsedAt)
			}.executeUpdate()
		}

		v
	}

	override fun update(v: ProfileSkin): Unit = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("UPDATE $TABLE_NAME SET " +
				"description = ?, " +
				"last_used_at = ? " +
				"WHERE profile_id = ? AND skin_id = ?").apply {
				this.setString(1, v.description)
				this.setObject(2, v.lastUsedAt)
				this.setInt(3, v.profile.id)
				this.setInt(4, v.skin.id)
			}.executeUpdate()
		}
	}

	override fun all(): Collection<ProfileSkin> = warnBlockingIO(plugin) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT * FROM $TABLE_NAME").executeQuery()
				.asSequence().map { fromRow(it) }.toList()
		}
	}

	private fun fromRow(result: ResultSet): ProfileSkin {
		val profile = profiles.forId(result.getInt("profile_id"))!!
		val skin = skins.forId(result.getInt("skin_id"))!!
		val lastUsedAt = result.getObject("last_used_at", Instant::class.java)
		val description = result.getString("description")

		return ProfileSkin(
			profile = profile,
			skin = skin,
			description = description,
			lastUsedAt = lastUsedAt
		)
	}
}
