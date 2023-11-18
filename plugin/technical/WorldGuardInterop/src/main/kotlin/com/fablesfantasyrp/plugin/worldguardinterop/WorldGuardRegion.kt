package com.fablesfantasyrp.plugin.worldguardinterop

import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.World

data class WorldGuardRegion(val world: World, val region: ProtectedRegion)
