package com.fablesfantasyrp.plugin.basicsystem.data

import com.fablesfantasyrp.plugin.basicsystem.data.entity.BasicSystemPlayer
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.PlayerRepository

interface BasicSystemPlayerRepository : MutableRepository<BasicSystemPlayer>, PlayerRepository<BasicSystemPlayer>
