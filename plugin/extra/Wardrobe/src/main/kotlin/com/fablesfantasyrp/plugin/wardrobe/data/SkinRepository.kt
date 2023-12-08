package com.fablesfantasyrp.plugin.wardrobe.data

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import org.bukkit.profile.PlayerTextures
import java.net.URL

interface SkinRepository : MutableRepository<Skin>, KeyedRepository<Int, Skin> {
	fun forValue(skinUrl: URL, skinModel: PlayerTextures.SkinModel): Skin
}
