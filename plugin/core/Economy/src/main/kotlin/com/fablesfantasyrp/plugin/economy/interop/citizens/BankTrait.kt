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
