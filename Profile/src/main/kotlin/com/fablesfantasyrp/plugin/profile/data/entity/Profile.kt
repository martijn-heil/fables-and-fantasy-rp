package com.fablesfantasyrp.plugin.profile.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.ProfileData
import com.fablesfantasyrp.plugin.utils.Services
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

class Profile : DataEntity<Int, Profile>, ProfileData {
	override var dirtyMarker: DirtyMarker<Profile>? = null
	var isDestroyed = false

	override val id: Int

	private var ownerUUID: UUID
	override var owner: OfflinePlayer
		set(value) { if (ownerUUID != value.uniqueId) { ownerUUID = value.uniqueId; dirtyMarker?.markDirty(this) } }
		get() = Bukkit.getOfflinePlayer(ownerUUID)
	override var description: String? set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var isActive: Boolean
		set(value) {
			if (field == value) return
			field = value
			dirtyMarker?.markDirty(this)
			if (!value) {
				val profileManager = Services.getMaybe<ProfileManager>() ?: return
				val currentPlayer = profileManager.getCurrentForProfile(this)
				if (currentPlayer != null) profileManager.stopTracking(currentPlayer)
			}
		}

	constructor(id: Int = -1,
				owner: OfflinePlayer,
				description: String?,
				isActive: Boolean,
				dirtyMarker: DirtyMarker<Profile>? = null) {
		this.id = id
		this.ownerUUID = owner.uniqueId
		this.owner = owner
		this.description = description
		this.isActive = isActive

		this.dirtyMarker = dirtyMarker
	}
}
