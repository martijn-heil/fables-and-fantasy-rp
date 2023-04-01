package com.fablesfantasyrp.plugin.weights

import org.bukkit.Material

data class WeightsConfig(val cap: Int, val additive: Map<Material, Int>, val singular: Map<Material, Int>)
