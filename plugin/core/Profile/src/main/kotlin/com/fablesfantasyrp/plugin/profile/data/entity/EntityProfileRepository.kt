package com.fablesfantasyrp.plugin.profile.data.entity

import com.fablesfantasyrp.plugin.database.entity.EntityRepository

interface EntityProfileRepository : EntityRepository<Int, Profile>, ProfileRepository {
}
