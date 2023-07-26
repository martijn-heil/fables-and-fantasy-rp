package com.fablesfantasyrp.plugin.wardrobe

import com.fablesfantasyrp.plugin.wardrobe.data.Skin
import org.bukkit.entity.Player

interface SkinService {
	fun setSkin(player: Player, skin: Skin?)
}
