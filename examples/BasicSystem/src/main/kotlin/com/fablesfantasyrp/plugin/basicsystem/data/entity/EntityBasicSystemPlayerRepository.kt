package com.fablesfantasyrp.plugin.basicsystem.data.entity

import com.fablesfantasyrp.plugin.basicsystem.data.BasicSystemPlayerRepository
import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import java.util.*

interface EntityBasicSystemPlayerRepository : EntityRepository<UUID, BasicSystemPlayer>, BasicSystemPlayerRepository
