package com.fablesfantasyrp.plugin.bell.dal.model

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.model.Named
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import dev.kord.common.entity.Snowflake

data class BellData(
	val location: BlockIdentifier,
	override val name: String,
	val discordChannelId: Snowflake,
	val discordRoleIds: Set<Snowflake>,
	override val id: Int = 0) : Identifiable<Int>, Named
