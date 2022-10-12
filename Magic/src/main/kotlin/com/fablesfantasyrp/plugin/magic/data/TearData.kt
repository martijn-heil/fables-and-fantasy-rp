package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.magic.MagicType
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import org.bukkit.Location

interface TearData : Identifiable<Long> {
	val magicType: MagicType
	val owner: Mage
	val location: Location
}
