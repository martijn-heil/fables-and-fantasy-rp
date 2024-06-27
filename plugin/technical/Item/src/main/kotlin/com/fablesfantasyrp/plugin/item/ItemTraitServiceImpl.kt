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
package com.fablesfantasyrp.plugin.item

import com.denizenscript.denizen.objects.ItemTag
import com.denizenscript.denizencore.objects.core.ListTag
import com.denizenscript.denizencore.objects.core.MapTag
import org.bukkit.inventory.ItemStack

class ItemTraitServiceImpl : ItemTraitService {
	override fun getTraits(item: ItemStack): Set<String> {
		val meta = ItemTag(item).flagTracker.getFlagValue("meta") as? MapTag ?: return emptySet()
		val traits = meta.getObject("traits") as? ListTag ?: return emptySet()
		return traits.toHashSet()
	}
}
