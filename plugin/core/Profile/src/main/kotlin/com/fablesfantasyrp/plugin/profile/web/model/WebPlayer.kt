package com.fablesfantasyrp.plugin.profile.web.model

import kotlinx.serialization.Serializable
import org.bukkit.OfflinePlayer

@Serializable
data class WebPlayer(val id: String,
					  val name: String?)
fun OfflinePlayer.transform() = WebPlayer(
	id = uniqueId.toString(),
	name = name
)
