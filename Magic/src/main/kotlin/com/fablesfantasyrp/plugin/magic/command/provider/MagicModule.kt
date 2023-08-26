package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterProvider
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicType
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.provider.EnumProvider
import org.bukkit.Server

class MagicModule(private val server: Server,
				  private val characters: EntityCharacterRepository,
				  private val mages: MageRepository,
				  private val spells: SpellDataRepository,
				  private val profileManager: ProfileManager) : AbstractModule() {
	override fun configure() {
		bind(Mage::class.java).toProvider(MageProvider(CharacterProvider(server, characters, profileManager), characters, mages))
		bind(Mage::class.java).annotatedWith(Sender::class.java).toProvider(MageSenderProvider(profileManager, characters, mages))
		bind(SpellData::class.java).annotatedWith(OwnSpell::class.java)
				.toProvider(OwnSpellDataProvider(SpellDataProvider(spells), profileManager, characters, mages, spells))
		bind(MagicType::class.java).toProvider(EnumProvider(MagicType::class.java))
		bind(MagicPath::class.java).toProvider(EnumProvider(MagicPath::class.java))
		bind(MageAbility::class.java).toProvider(MageAbilityProvider(profileManager, characters, mages))
	}
}
