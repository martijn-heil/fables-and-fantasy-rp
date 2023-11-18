package com.fablesfantasyrp.plugin.wardrobe.data

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import java.time.Instant

data class ProfileSkin(val profile: Profile,
					   val skin: Skin,
					   val description: String,
					   val lastUsedAt: Instant?)
