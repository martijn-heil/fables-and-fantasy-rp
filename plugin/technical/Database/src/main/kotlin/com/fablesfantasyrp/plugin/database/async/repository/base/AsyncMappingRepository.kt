package com.fablesfantasyrp.plugin.database.async.repository.base

import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class AsyncMappingRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(private val child: ChildRepositoryType)
	: AsyncMutableRepository<ThisType>, AsyncKeyedRepository<KeyType, ThisType>, HasDestroyHandler<ThisType>
		where 	ThisType : Identifiable<KeyType>,
				ChildType : Identifiable<KeyType>,
				ChildRepositoryType: MutableRepository<ChildType>,
				ChildRepositoryType: KeyedRepository<KeyType, ChildType> {
	private val destroyHandlers = ArrayList<(ThisType) -> Unit>()

	abstract fun convertFromChild(v: ChildType): ThisType
	abstract fun convertToChild(v: ThisType): ChildType

	override suspend fun create(v: ThisType): ThisType
		= convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.create(it) } }
			.let { convertFromChild(it) }

	override suspend fun update(v: ThisType)
		= convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.update(it) } }

	override suspend fun allIds(): Collection<KeyType>
		= withContext(Dispatchers.IO) { child.allIds() }

	override suspend fun all(): Collection<ThisType>
		= withContext(Dispatchers.IO) { child.all() }
			.map { convertFromChild(it) }

	override suspend fun forId(id: KeyType): ThisType?
		= withContext(Dispatchers.IO) { child.forId(id) }
			?.let { convertFromChild(it) }

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
