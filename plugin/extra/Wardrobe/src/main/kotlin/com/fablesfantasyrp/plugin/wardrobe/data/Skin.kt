package com.fablesfantasyrp.plugin.wardrobe.data

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import com.fablesfantasyrp.plugin.database.model.Identifiable

data class Skin(val value: String, val signature: String, override val id: Int = 0) : Identifiable<Int> {
	fun toProfileProperty() = ProfileProperty("textures", value, signature)
}

fun ProfileProperty.toSkin(): Skin = Skin(value, signature!!)
val PlayerProfile.skin get() = properties.find { it.name == "textures" }!!.toSkin()
