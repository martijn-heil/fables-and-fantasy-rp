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
package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.caturix.parametric.AbstractModule
import com.fablesfantasyrp.caturix.parametric.provider.EnumProvider
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterProvider
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.authorizer.MagicTypeAuthorizer
import com.fablesfantasyrp.plugin.magic.authorizer.SpellAuthorizer
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicType
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.Server

class MagicModule(private val server: Server,
				  private val characters: CharacterRepository,
				  private val mages: MageRepository,
				  private val spells: SpellDataRepository,
				  private val spellAuthorizer: SpellAuthorizer,
				  private val magicTypeAuthorizer: MagicTypeAuthorizer,
				  private val profileManager: ProfileManager) : AbstractModule() {
	override fun configure() {
		bind(Mage::class.java).toProvider(MageProvider(CharacterProvider(server, characters, profileManager), mages))
		bind(Mage::class.java).annotatedWith(Sender::class.java).toProvider(MageSenderProvider(profileManager, characters, mages))
		bind(SpellData::class.java).annotatedWith(OwnSpell::class.java)
				.toProvider(OwnSpellDataProvider(SpellDataProvider(spells), profileManager, characters, spellAuthorizer))
		bind(MagicType::class.java).toProvider(EnumProvider(MagicType::class.java))
		bind(MagicType::class.java).annotatedWith(OwnMagicType::class.java)
			.toProvider(OwnMagicTypeProvider(profileManager, characters, EnumProvider(MagicType::class.java), magicTypeAuthorizer))
		bind(MagicPath::class.java).toProvider(EnumProvider(MagicPath::class.java))
		bind(MageAbility::class.java).toProvider(MageAbilityProvider(profileManager, characters, mages))
	}
}
