package com.fablesfantasyrp.plugin.basicsystem.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

class EntitySlidingDoorRepositoryImpl<C>(child: C) : MassivelyCachingEntityRepository<Int, SlidingDoor, C>(child), EntitySlidingDoorRepository
		where C: KeyedRepository<Int, SlidingDoor>,
			  C: MutableRepository<SlidingDoor>,
			  C: HasDirtyMarker<SlidingDoor>,
			  C: SlidingDoorRepository {

}
