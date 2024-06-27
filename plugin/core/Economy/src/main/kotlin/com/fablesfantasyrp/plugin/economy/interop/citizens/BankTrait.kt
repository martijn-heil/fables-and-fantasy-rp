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
package com.fablesfantasyrp.plugin.economy.interop.citizens

import com.fablesfantasyrp.plugin.economy.PLUGIN
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.gui.bank.BankGuiMainMenu
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.Services
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import net.citizensnpcs.api.trait.TraitInfo
import net.citizensnpcs.api.trait.TraitName
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

@TraitName("fables-banker")
class CitizensFablesBankerTrait : Trait("fables-banker") {
	// An example event handler. All traits will be registered automatically as Spigot event Listeners
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onRightClickNPC(e: NPCRightClickEvent){
		if (e.npc != this.npc) return

		if (e.npc.hasTrait(CitizensFablesBankerTrait::class.java)) {
			val profileManager = Services.get<ProfileManager>()
			val economyRepository = Services.get<EntityProfileEconomyRepository>()

			val profile = profileManager.getCurrentForPlayer(e.clicker) ?: return
			val economy = economyRepository.forProfile(profile)
			BankGuiMainMenu(PLUGIN, economy).show(e.clicker)
		}
	}
}

class CitizensHook {
	private lateinit var traitInfo: TraitInfo

	fun start() {
		traitInfo = TraitInfo.create(CitizensFablesBankerTrait::class.java)
		CitizensAPI.getTraitFactory().registerTrait(traitInfo)
	}

	fun stop() {
		CitizensAPI.getTraitFactory().deregisterTrait(traitInfo)
	}
}
