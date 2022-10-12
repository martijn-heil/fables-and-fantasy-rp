package com.fablesfantasyrp.plugin.database.repository

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.norm
import com.fablesfantasyrp.plugin.database.PokoInfo

open class NormRepository<K, T: Identifiable<K>>(private val keyClazz: Class<K>, private val subjectClazz: Class<T>)
	: MutableRepository<T>, KeyedRepository<K, T> {
	private val pojoInfo = PokoInfo(subjectClazz.kotlin)

	//override fun forId(id: K): T? = norm.table(pojoInfo.table).where("${pojoInfo.primaryKeyNames.first()}=?", id).first(subjectClazz)
	override fun forId(id: K): T? = norm.table(pojoInfo.table).where("id=?", id).first(subjectClazz)
	override fun allIds(): Collection<K> = norm.sql("select id from ${pojoInfo.table}").results(keyClazz)
	override fun all(): Collection<T> = norm.table(pojoInfo.table).results(subjectClazz)
	override fun destroy(v: T) { norm.table(pojoInfo.table).delete(v) }
	override fun create(v: T): T { norm.table(pojoInfo.table).insert(v); return v }
	override fun update(v: T) { norm.table(pojoInfo.table).update(v) }
}
