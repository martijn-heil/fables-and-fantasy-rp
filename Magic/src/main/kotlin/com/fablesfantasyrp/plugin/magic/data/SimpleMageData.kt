package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.magic.MagicPath

data class SimpleMageData(override var id: Long,
						  override val magicLevel: Int,
						  override val magicPath: MagicPath,
						  override val spells: List<SpellData>) : MageData
