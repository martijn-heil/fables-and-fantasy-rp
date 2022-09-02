package com.fablesfantasyrp.plugin.knockout

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

fun getFirstBlockBelowLocation(loc: Location): Block {
	var block = loc.block
	while (block.type == Material.AIR) {
		val relative = block.getRelative(BlockFace.DOWN)
		if (relative.y < loc.world.minHeight) break
		block = relative
	}

	return block
}
