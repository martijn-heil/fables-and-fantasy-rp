package com.fablesfantasyrp.plugin.party.command.provider

import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.party.PartyColor
import com.fablesfantasyrp.plugin.party.PartySpectatorManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.party.data.entity.Party
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.provider.EnumProvider

class PartyModule(private val parties: PartyRepository,
				  private val characters: CharacterRepository,
				  private val profileManager: ProfileManager,
				  private val spectatorManager: PartySpectatorManager) : AbstractModule() {
	override fun configure() {
		bind(Party::class.java).toProvider(PartyProvider(parties, characters, profileManager, spectatorManager))
		bind(PartyColor::class.java).toProvider(EnumProvider(PartyColor::class.java))
	}
}
