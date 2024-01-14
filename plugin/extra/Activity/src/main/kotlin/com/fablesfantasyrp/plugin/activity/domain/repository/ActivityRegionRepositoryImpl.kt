package com.fablesfantasyrp.plugin.activity.domain.repository

import com.fablesfantasyrp.plugin.activity.domain.entity.ActivityRegion
import com.fablesfantasyrp.plugin.activity.domain.mapper.ActivityRegionMapper
import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncTypicalImmutableRepository

class ActivityRegionRepositoryImpl(child: ActivityRegionMapper)
	: AsyncTypicalImmutableRepository<String, ActivityRegion, ActivityRegionMapper>(child), ActivityRegionRepository {

}
