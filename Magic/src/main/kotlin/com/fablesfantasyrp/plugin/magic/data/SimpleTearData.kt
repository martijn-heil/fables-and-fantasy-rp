package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.magic.MagicType
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import org.bukkit.Location

data class SimpleTearData(override val id: Long,
						  override val magicType: MagicType,
						  override val owner: Mage,
						  override val location: Location) : TearData
