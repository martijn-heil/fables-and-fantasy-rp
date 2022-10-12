package com.fablesfantasyrp.plugin.database

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

abstract class MappingRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(private val child: ChildRepositoryType)
	: MutableRepository<ThisType>, KeyedRepository<KeyType, ThisType>

		where 	ThisType : Identifiable<KeyType>,
				ChildType : Identifiable<KeyType>,
			  	ChildRepositoryType: MutableRepository<ChildType>,
			  	ChildRepositoryType: KeyedRepository<KeyType, ChildType> {
	abstract fun convertFromChild(v: ChildType): ThisType
	abstract fun convertToChild(v: ThisType): ChildType
	override fun destroy(v: ThisType) = child.destroy(convertToChild(v))
	override fun create(v: ThisType): ThisType = convertFromChild(child.create(convertToChild(v)))
	override fun update(v: ThisType) = child.update(convertToChild(v))
	override fun allIds(): Collection<KeyType> = child.allIds()
	override fun all(): Collection<ThisType> = child.all().map { convertFromChild(it) }
	override fun forId(id: KeyType): ThisType? = child.forId(id)?.let { convertFromChild(it) }
}
