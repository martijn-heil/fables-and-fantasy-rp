package com.fablesfantasyrp.plugin.database.async.repository.base

import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class AsyncMappingImmutableRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(protected val child: ChildRepositoryType)
	: AsyncKeyedRepository<KeyType, ThisType>
		where 	ThisType : Identifiable<KeyType>,
				ChildType : Identifiable<KeyType>,
				ChildRepositoryType: KeyedRepository<KeyType, ChildType> {

	abstract fun convertFromChild(v: ChildType): ThisType

	override suspend fun allIds(): Collection<KeyType>
		= withContext(Dispatchers.IO) { child.allIds() }

	override suspend fun all(): Collection<ThisType>
		= withContext(Dispatchers.IO) { child.all() }
			.map { convertFromChild(it) }

	override suspend fun forId(id: KeyType): ThisType?
		= withContext(Dispatchers.IO) { child.forId(id) }
			?.let { convertFromChild(it) }
}
