package com.fablesfantasyrp.plugin.domain

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.*


val FABLES_ADMIN = Bukkit.getOfflinePlayer(UUID.fromString("bcdb5a59-269e-43df-914b-eed888597272"))
val SPAWN: Location get() = EDEN!!.spawnLocation.toCenterLocation()
val FLATROOM: World? get() = Bukkit.getWorld("flatroom")
val PLOTS: World? get() = Bukkit.getWorld("plots")
val EDEN: World? get() = Bukkit.getWorld("Eden")

const val DISTANCE_WHISPER = 2U
const val DISTANCE_QUIET = 8u
const val DISTANCE_TALK = 15U
const val DISTANCE_SHOUT = 30U