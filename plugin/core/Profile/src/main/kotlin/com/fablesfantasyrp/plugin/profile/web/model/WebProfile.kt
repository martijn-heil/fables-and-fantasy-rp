package com.fablesfantasyrp.plugin.profile.web.model

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import kotlinx.serialization.Serializable

@Serializable
data class WebProfile(val id: Int,
					  val description: String?,
					  val owner: String?)
fun Profile.transform() = WebProfile(
	id = id,
	description = description,
	owner = owner?.uniqueId?.toString()
)
