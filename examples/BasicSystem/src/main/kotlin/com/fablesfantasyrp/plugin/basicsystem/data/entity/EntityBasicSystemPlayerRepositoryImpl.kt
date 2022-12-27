package com.fablesfantasyrp.plugin.basicsystem.data.entity

import com.fablesfantasyrp.plugin.basicsystem.data.BasicSystemPlayerRepository
import com.fablesfantasyrp.plugin.database.entity.EntityPlayerRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import org.bukkit.plugin.Plugin

class EntityBasicSystemPlayerRepositoryImpl<C>(child: C, plugin: Plugin)
	: EntityPlayerRepository<C, BasicSystemPlayer>(child, plugin), EntityBasicSystemPlayerRepository
	where C: BasicSystemPlayerRepository, C: HasDirtyMarker<BasicSystemPlayer>
