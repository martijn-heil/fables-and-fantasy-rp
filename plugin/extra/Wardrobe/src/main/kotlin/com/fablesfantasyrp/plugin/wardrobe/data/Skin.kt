/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.wardrobe.data

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import com.fablesfantasyrp.plugin.database.model.Identifiable

data class Skin(val value: String, val signature: String, override val id: Int = 0) : Identifiable<Int> {
	fun toProfileProperty() = ProfileProperty("textures", value, signature)
}

fun ProfileProperty.toSkin(): Skin = Skin(value, signature!!)
val PlayerProfile.skin get() = properties.find { it.name == "textures" }!!.toSkin()
