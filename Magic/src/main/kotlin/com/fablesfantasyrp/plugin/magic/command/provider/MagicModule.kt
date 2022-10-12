package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.command.provider.PlayerCharacterProvider
import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.MagicType
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellData
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.spellRepository
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.parametric.AbstractModule
import com.sk89q.intake.parametric.provider.EnumProvider
import org.bukkit.Server

class MagicModule(private val server: Server) : AbstractModule() {
	override fun configure() {
		bind(Mage::class.java).toProvider(MageProvider(PlayerCharacterProvider(server)))
		bind(Mage::class.java).annotatedWith(Sender::class.java).toProvider(MageSenderProvider())
		bind(SimpleSpellData::class.java).annotatedWith(OwnSpell::class.java)
				.toProvider(OwnSpellDataProvider(SpellDataProvider(spellRepository)))
		bind(MagicType::class.java).toProvider(EnumProvider(MagicType::class.java))
		bind(MagicPath::class.java).toProvider(EnumProvider(MagicPath::class.java))
	}
}
