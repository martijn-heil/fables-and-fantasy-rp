package com.fablesfantasyrp.plugin.warp.command.provider

import com.fablesfantasyrp.plugin.warp.data.SimpleWarp
import com.fablesfantasyrp.plugin.warp.data.SimpleWarpRepository
import com.fablesfantasyrp.caturix.parametric.AbstractModule


class WarpModule(private val warps: SimpleWarpRepository) : AbstractModule() {
	override fun configure() {
		bind(SimpleWarp::class.java).toProvider(WarpProvider(warps))
	}
}
