package com.fablesfantasyrp.plugin.utils

import com.earth2me.essentials.User
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

// This code is supported by SuperVanish, PremiumVanish, VanishNoPacket and a few more vanish plugins.
val Player.isVanished: Boolean
	get() = getMetadata("vanished").find { it.asBoolean() } != null

val OfflinePlayer.ess: User
	get() = essentials.getUser(uniqueId)
