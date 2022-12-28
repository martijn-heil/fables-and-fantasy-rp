package com.fablesfantasyrp.plugin.basicsystem.data

import org.bukkit.Material
import java.io.Serializable

class OffsetBlock(val deltaX: Int,
				  val deltaY: Int,
				  val deltaZ: Int,
				  val material: Material) : Serializable {
	companion object {
		private const val serialVersionUID: Long = 1
	}
}
