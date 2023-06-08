package com.fablesfantasyrp.plugin.bell.data.entity

import com.fablesfantasyrp.plugin.database.entity.EntityRepository

interface EntityBellRepository : EntityRepository<Int, Bell>, BellRepository
