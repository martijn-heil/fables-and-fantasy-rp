package com.fablesfantasyrp.plugin.database.async.repository.base

import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class AsyncMappingRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(child: ChildRepositoryType)
	: AsyncMappingImmutableRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(child),
	AsyncMutableRepository<ThisType>, AsyncKeyedRepository<KeyType, ThisType>, HasDestroyHandler<ThisType>
		where 	ThisType : Identifiable<KeyType>,
				ChildType : Identifiable<KeyType>,
				ChildRepositoryType: MutableRepository<ChildType>,
				ChildRepositoryType: KeyedRepository<KeyType, ChildType> {
	private val destroyHandlers = ArrayList<(ThisType) -> Unit>()

	abstract fun convertToChild(v: ThisType): ChildType

	override suspend fun create(v: ThisType): ThisType
		= convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.create(it) } }
			.let { convertFromChild(it) }

	override suspend fun update(v: ThisType)
		= convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.update(it) } }

	override suspend fun createOrUpdate(v: ThisType): ThisType
		= convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.createOrUpdate(it) } }
			.let { convertFromChild(it) }

	override suspend fun destroy(v: ThisType) {
		destroyHandlers.forEach { it(v) }
		convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.destroy(it) } }
	}

	override fun onDestroy(callback: (ThisType) -> Unit) {
		destroyHandlers.add(callback)
	}
}
