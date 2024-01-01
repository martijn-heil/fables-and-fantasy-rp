package com.fablesfantasyrp.plugin.database.sync.repository.base

import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository

abstract class MappingRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(private val child: ChildRepositoryType)
	: MutableRepository<ThisType>, KeyedRepository<KeyType, ThisType>, HasDestroyHandler<ThisType>
		where 	ThisType : Identifiable<KeyType>,
				ChildType : Identifiable<KeyType>,
				ChildRepositoryType: MutableRepository<ChildType>,
				ChildRepositoryType: KeyedRepository<KeyType, ChildType> {
	private val destroyHandlers = ArrayList<(ThisType) -> Unit>()

	abstract fun convertFromChild(v: ChildType): ThisType
	abstract fun convertToChild(v: ThisType): ChildType
	override fun create(v: ThisType): ThisType = convertFromChild(child.create(convertToChild(v)))
	override fun update(v: ThisType) = child.update(convertToChild(v))
	override fun allIds(): Collection<KeyType> = child.allIds()
	override fun all(): Collection<ThisType> = child.all().map { convertFromChild(it) }
	override fun forId(id: KeyType): ThisType? = child.forId(id)?.let { convertFromChild(it) }
	override fun createOrUpdate(v: ThisType): ThisType = convertFromChild(child.createOrUpdate(convertToChild(v)))

	override fun destroy(v: ThisType) {
		destroyHandlers.forEach { it(v) }
		child.destroy(convertToChild(v))
	}

	override fun onDestroy(callback: (ThisType) -> Unit) {
		destroyHandlers.add(callback)
	}
}