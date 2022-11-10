package com.fablesfantasyrp.plugin.fasttravel

import com.sk89q.worldedit.math.BlockVector3
import org.bukkit.Location

fun Location.toBlockVector3() = BlockVector3.at(x, y, z)
