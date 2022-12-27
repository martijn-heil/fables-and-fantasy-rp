package com.fablesfantasyrp.plugin.database

import org.h2.api.H2Type
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*


fun<T> ResultSet.getList(column: String): List<T>  {
	return (this.getArray(column).array as Array<*>).map { it as T }
}

fun<T> ResultSet.getList(column: Int): List<T>  {
	return (this.getArray(column).array as Array<*>).map { it as T }
}

inline fun<reified T> PreparedStatement.setList(n: Int, sqlDataType: H2Type, collection: List<T>) {
	this.setArray(n, this.connection.createArrayOf("${sqlDataType.name} ARRAY", collection.toTypedArray()))
}

inline fun<reified T> PreparedStatement.setCollection(n: Int, sqlDataType: H2Type, collection: Collection<T>) {
	this.setArray(n, this.connection.createArrayOf("${sqlDataType.name} ARRAY", collection.toTypedArray()))
}

fun ResultSet.getUuid(column: String) = this.getObject(column, UUID::class.java)
fun ResultSet.getUuid(column: Int) = this.getObject(column, UUID::class.java)
fun PreparedStatement.setUuid(n: Int, uuid: UUID) = this.setObject(n, uuid)
