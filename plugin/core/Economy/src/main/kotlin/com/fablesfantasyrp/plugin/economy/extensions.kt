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
package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomy
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.koin.core.context.GlobalContext

var Profile.money: Int
	get() = GlobalContext.get().get<ProfileEconomyRepository>().forProfile(this).money
	set(value) { GlobalContext.get().get<ProfileEconomyRepository>().forProfile(this).money = value }

val Profile.economy: ProfileEconomy
	get() = GlobalContext.get().get<ProfileEconomyRepository>().forProfile(this)
