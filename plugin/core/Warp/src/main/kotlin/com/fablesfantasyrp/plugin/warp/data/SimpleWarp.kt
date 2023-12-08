package com.fablesfantasyrp.plugin.warp.data

import com.fablesfantasyrp.plugin.database.model.Identifiable
import org.bukkit.Location

data class SimpleWarp(override val id: String, val location: Location) : Identifiable<String>
