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
package com.fablesfantasyrp.plugin.inventory.domain.repository

import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncTypicalRepository
import com.fablesfantasyrp.plugin.inventory.domain.entity.ProfileInventory
import com.fablesfantasyrp.plugin.inventory.domain.mapper.ProfileInventoryMapper
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

class ProfileInventoryRepositoryImpl(child: ProfileInventoryMapper)
	: AsyncTypicalRepository<Int, ProfileInventory, ProfileInventoryMapper>(child), ProfileInventoryRepository {

	override suspend fun forOwner(owner: Profile): ProfileInventory
		= forId(owner.id) ?: deduplicate(child.forOwner(owner))
}
