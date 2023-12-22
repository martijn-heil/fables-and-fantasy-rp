package com.fablesfantasyrp.plugin.wardrobe.data

import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface ProfileSkinRepository : MutableRepository<ProfileSkin> {
	fun forProfile(profile: Profile): Collection<ProfileSkin>
	fun getLastUsed(profile: Profile): ProfileSkin?
}
