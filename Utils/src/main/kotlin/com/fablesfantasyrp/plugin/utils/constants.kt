package com.fablesfantasyrp.plugin.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.World
import java.util.*


val FABLES_ADMIN = Bukkit.getOfflinePlayer(UUID.fromString("bcdb5a59-269e-43df-914b-eed888597272"))
val SPAWN: Location = EDEN!!.spawnLocation
val FLATROOM: World? get() = Bukkit.getWorld("flatroom")
val PLOTS: World? get() = Bukkit.getWorld("plots")
val EDEN: World? get() = Bukkit.getWorld("Eden")
val GLOBAL_SYSPREFIX = "$DARK_GRAY$BOLD[$DARK_PURPLE${BOLD}i$DARK_GRAY$BOLD]$GRAY"
