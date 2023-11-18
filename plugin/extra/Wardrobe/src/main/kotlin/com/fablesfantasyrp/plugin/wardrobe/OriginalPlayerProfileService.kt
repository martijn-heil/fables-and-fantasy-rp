package com.fablesfantasyrp.plugin.wardrobe

import com.destroystokyo.paper.profile.PlayerProfile
import org.bukkit.entity.Player
import java.util.*

interface OriginalPlayerProfileService {
	fun getOriginalPlayerProfile(uuid: UUID): PlayerProfile?
	fun getOriginalPlayerProfile(player: Player): PlayerProfile? = getOriginalPlayerProfile(player.uniqueId)
}
