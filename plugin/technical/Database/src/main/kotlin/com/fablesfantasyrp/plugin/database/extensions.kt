/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.database

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.LoadingCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.h2.api.H2Type
import java.lang.ref.SoftReference
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*
import kotlin.collections.Map


inline fun<reified T> ResultSet.getList(column: String): List<T>  {
	return (this.getArray(column).array as Array<*>).map { it as T }
}

inline fun<reified T> ResultSet.getList(column: Int): List<T>  {
	return (this.getArray(column).array as Array<*>).map { it as T }
}

inline fun<reified T> PreparedStatement.setList(n: Int, sqlDataType: H2Type, collection: List<T>) {
	this.setArray(n, this.connection.createArrayOf("${sqlDataType.name} ARRAY", collection.toTypedArray()))
}

inline fun<reified T> PreparedStatement.setCollection(n: Int, sqlDataType: H2Type, collection: Collection<T>) {
	this.setArray(n, this.connection.createArrayOf("${sqlDataType.name} ARRAY", collection.toTypedArray()))
}

fun ResultSet.getUuid(column: String) = this.getObject(column) as? UUID
fun ResultSet.getUuid(column: Int) = this.getObject(column) as? UUID
fun PreparedStatement.setUuid(n: Int, uuid: UUID?) = this.setObject(n, uuid, H2Type.UUID)

fun ResultSet.asSequence(): Sequence<ResultSet> = generateSequence { if (this.next()) this else null }

suspend fun<K, T> MutableMap<K, SoftReference<T>>.getOrLoad(key: K, load: (K) -> T?): T? {
	val value = this[key]?.get()

	return if (value == null) {
		val newValue = withContext(Dispatchers.IO) { load(key) }

		val secondTry = this[key]?.get()
		if (secondTry != null) return secondTry

		if (newValue != null) {
			this[key] = SoftReference(newValue)
			newValue
		} else {
			this.remove(key)
			null
		}
	} else {
		value
	}
}

suspend fun<K, V> LoadingCache<K, V>.getOrLoadAsync(key: K): V? {
	return this.getIfPresent(key) ?: withContext(Dispatchers.IO) { this@getOrLoadAsync.get(key) }
}
