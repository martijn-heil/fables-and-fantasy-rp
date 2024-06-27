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
package com.fablesfantasyrp.plugin.denizeninterop

import com.denizenscript.denizen.objects.EntityTag
import com.denizenscript.denizen.objects.ItemTag
import com.denizenscript.denizen.objects.PlayerTag
import com.denizenscript.denizencore.flags.AbstractFlagTracker
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

val Entity.dFlagsEntity: AbstractFlagTracker
	get() = EntityTag(this).flagTracker

val OfflinePlayer.dFlags: AbstractFlagTracker
	get() = PlayerTag(this).flagTracker

val ItemStack.dFlags: AbstractFlagTracker
	get() = ItemTag(this).flagTracker

val Server.dFlags: AbstractFlagTracker
	get() = TODO()

fun example() {
	val entity = Bukkit.getServer().worlds.first().entities.first()
	val t = EntityTag(entity)
	entity.dFlagsEntity.getFlagValue("test").asElement().asString()
}
