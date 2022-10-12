package com.fablesfantasyrp.plugin.database

import com.dieselpoint.norm.DbException
import com.dieselpoint.norm.Query
import com.dieselpoint.norm.Util
import com.dieselpoint.norm.sqlmakers.SqlMaker
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KType
import kotlin.reflect.full.createType


/**
 * Produces ANSI-standard SQL. Extend this class to handle different flavors of
 * sql.
 */
open class FablesSQLMaker : SqlMaker {
	override fun getPojoInfo(rowClass: Class<*>): PokoInfo {
		var pi: PokoInfo? = map[rowClass]
		if (pi == null) {
			pi = PokoInfo(rowClass.kotlin)
			map[rowClass] = pi
			makeInsertSql(pi)
			makeUpsertSql(pi)
			makeUpdateSql(pi)
			makeSelectColumns(pi)
		}
		return pi
	}

	override fun getInsertSql(query: Query, row: Any): String {
		val pojoInfo = getPojoInfo(row.javaClass)
		return String.format(pojoInfo.insertSql!!, Objects.requireNonNullElse(query.table, pojoInfo.table))
	}

	override fun getInsertArgs(query: Query, row: Any): Array<Any?> {
		val pojoInfo = getPojoInfo(row.javaClass)
		val args = arrayOfNulls<Any>(pojoInfo.insertSqlArgCount)
		for (i in 0 until pojoInfo.insertSqlArgCount) {
			args[i] = pojoInfo.getValue(row, pojoInfo.insertColumnNames[i])
		}
		return args
	}

	override fun getUpdateSql(query: Query, row: Any): String {
		val pojoInfo = getPojoInfo(row.javaClass)
		if (pojoInfo.primaryKeyNames.isEmpty()) {
			throw DbException("No primary keys specified in the row. Use the @Id annotation.")
		}
		return String.format(pojoInfo.updateSql!!, Objects.requireNonNullElse(query.table, pojoInfo.table))
	}

	override fun getUpdateArgs(query: Query, row: Any): Array<Any?> {
		val pojoInfo = getPojoInfo(row.javaClass)
		val numKeys = pojoInfo.primaryKeyNames.size
		val args = arrayOfNulls<Any>(pojoInfo.updateSqlArgCount)
		for (i in 0 until pojoInfo.updateSqlArgCount - numKeys) {
			args[i] = pojoInfo.getValue(row, pojoInfo.updateColumnNames[i])
		}
		// add the value for the where clause to the end
		for (i in 0 until numKeys) {
			val pk = pojoInfo.getValue(row, pojoInfo.primaryKeyNames[i])
			args[pojoInfo.updateSqlArgCount - (numKeys - i)] = pk
		}
		return args
	}

	fun makeUpdateSql(pojoInfo: PokoInfo) {
		val cols = ArrayList<String>()
		for (prop in pojoInfo.propertyMap.values) {
			if (prop.isPrimaryKey) {
				continue
			}
			if (prop.isGenerated) {
				continue
			}
			cols.add(prop.name)
		}
		pojoInfo.updateColumnNames = cols.toTypedArray()
		pojoInfo.updateSqlArgCount = pojoInfo.updateColumnNames.size + pojoInfo.primaryKeyNames.size // + # of primary keys for the where arg
		val buf = StringBuilder()
		buf.append("update %s set ")
		for (i in cols.indices) {
			if (i > 0) {
				buf.append(',')
			}
			buf.append(cols[i]).append("=?")
		}
		buf.append(" where ")
		for (i in pojoInfo.primaryKeyNames.indices) {
			if (i > 0) {
				buf.append(" and ")
			}
			buf.append(pojoInfo.primaryKeyNames[i]).append("=?")
		}
		pojoInfo.updateSql = buf.toString()
	}

	fun makeInsertSql(pojoInfo: PokoInfo) {
		val cols = ArrayList<String>()
		for (prop in pojoInfo.propertyMap.values) {
			if (prop.isGenerated) {
				continue
			}
			cols.add(prop.name)
		}
		pojoInfo.insertColumnNames = cols.toTypedArray()
		pojoInfo.insertSqlArgCount = pojoInfo.insertColumnNames.size
		pojoInfo.insertSql = "insert into %s (" + Util.join(pojoInfo.insertColumnNames) +  // comma sep list?
				") values (" + Util.getQuestionMarks(pojoInfo.insertSqlArgCount) + ")"
	}

	open fun makeUpsertSql(pojoInfo: PokoInfo?) {}
	private fun makeSelectColumns(pojoInfo: PokoInfo) {
		if (pojoInfo.propertyMap.isEmpty()) {
			// this applies if the rowClass is a Map
			pojoInfo.selectColumns = "*"
		} else {
			val cols = ArrayList<String>()
			for (prop in pojoInfo.propertyMap.values) {
				cols.add(prop.name)
			}
			pojoInfo.selectColumns = Util.join(cols)
		}
	}

	override fun getSelectSql(query: Query, rowClass: Class<*>): String {

		// unlike insert and update, this needs to be done dynamically
		// and can't be precalculated because of the where and order by
		val pojoInfo = getPojoInfo(rowClass)
		val columns = pojoInfo.selectColumns
		val where = query.where
		var table = query.table
		if (table == null) {
			table = pojoInfo.table
		}
		val orderBy = query.orderBy
		val out = StringBuilder()
		out.append("select ")
		out.append(columns)
		out.append(" from ")
		out.append(table)
		if (where != null) {
			out.append(" where ")
			out.append(where)
		}
		if (orderBy != null) {
			out.append(" order by ")
			out.append(orderBy)
		}
		return out.toString()
	}

	override fun getCreateTableSql(clazz: Class<*>): String {
		val buf = StringBuilder()
		val pojoInfo = getPojoInfo(clazz)
		buf.append("create table ")
		buf.append(pojoInfo.table)
		buf.append(" (")
		var needsComma = false
		for (prop in pojoInfo.propertyMap.values) {
			if (needsComma) {
				buf.append(',')
			}
			needsComma = true
			val columnAnnot = prop.columnAnnotation
			if (columnAnnot == null) {
				buf.append(prop.name)
				buf.append(" ")
				buf.append(getColType(prop.dataType!!, 255, 10, 2))
				if (prop.isGenerated) {
					buf.append(" auto_increment")
				}
			} else {
				if (columnAnnot.columnDefinition == null) {

					// let the column def override everything
					buf.append(columnAnnot.columnDefinition)
				} else {
					buf.append(prop.name)
					buf.append(" ")
					buf.append(getColType(prop.dataType, columnAnnot.length, columnAnnot.precision,
							columnAnnot.scale))
					if (prop.isGenerated) {
						buf.append(" auto_increment")
					}
					if (columnAnnot.unique) {
						buf.append(" unique")
					}
					if (!columnAnnot.nullable) {
						buf.append(" not null")
					}
				}
			}
		}
		if (pojoInfo.primaryKeyNames.size > 0) {
			buf.append(", primary key (")
			for (i in pojoInfo.primaryKeyNames.indices) {
				if (i > 0) {
					buf.append(",")
				}
				buf.append(pojoInfo.primaryKeyNames[i])
			}
			buf.append(")")
		}
		buf.append(")")
		return buf.toString()
	}

	protected open fun getColType(dataType: KType, length: Int, precision: Int, scale: Int): String? {
		return  when (dataType) {
			Int::class.createType() -> "integer"
			Long::class.createType() -> "bigint"
			Double::class.createType() -> "double"
			Float::class.createType() -> "float"
			BigDecimal::class.createType() -> "decimal($precision,$scale)"
			Date::class.createType() -> "datetime"
			else -> "varchar($length)"
		}
	}

	override fun convertValue(value: Any, columnTypeName: String): Any {
		return value
	}

	override fun getDeleteSql(query: Query, row: Any): String {
		val pojoInfo = getPojoInfo(row.javaClass)
		var table = query.table
		if (table == null) {
			table = pojoInfo.table
			if (table == null) {
				throw DbException("You must specify a table name")
			}
		}
		val builder = StringBuilder("delete from ")
		builder.append(table).append(" where ")
		for (i in pojoInfo.primaryKeyNames.indices) {
			if (i > 0) {
				builder.append(" and ")
			}
			builder.append(pojoInfo.primaryKeyNames[i]).append("=?")
		}
		return builder.toString()
	}

	override fun getDeleteArgs(query: Query, row: Any): Array<Any?> {
		val pojoInfo = getPojoInfo(row.javaClass)
		val args = arrayOfNulls<Any>(pojoInfo.primaryKeyNames.size)
		for (i in pojoInfo.primaryKeyNames.indices) {
			val primaryKeyValue = pojoInfo.getValue(row, pojoInfo.primaryKeyNames[i])
			args[i] = primaryKeyValue
		}
		return args
	}

	override fun getUpsertSql(query: Query, row: Any): String {
		val msg = ("There's no standard upsert implemention. There is one in the MySql driver, though,"
				+ "so if you're using MySql, call Database.setSqlMaker(new MySqlMaker()); Or roll your own.")
		throw UnsupportedOperationException(msg)
	}

	override fun getUpsertArgs(query: Query, row: Any): Array<Any> {
		throw UnsupportedOperationException()
	}

	companion object {
		private val map = ConcurrentHashMap<Class<*>, PokoInfo>()
	}
}
