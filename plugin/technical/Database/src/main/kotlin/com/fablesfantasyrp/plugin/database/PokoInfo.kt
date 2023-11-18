package com.fablesfantasyrp.plugin.database

import com.dieselpoint.norm.ColumnOrder
import com.dieselpoint.norm.DbException
import com.dieselpoint.norm.sqlmakers.PojoInfo
import com.dieselpoint.norm.sqlmakers.Property
import java.beans.IntrospectionException
import javax.persistence.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

/*
 * annotations recognized: @ Id, @ GeneratedValue @ Transient @ Table @ Column @
 * DbSerializer @ Enumerated
 */
class PokoInfo(clazz: KClass<*>) : PojoInfo {
	var propertyMap: MutableMap<String, FablesKProperty> = LinkedHashMap()
	var table: String? = null
	var primaryKeyNames: MutableList<String> = ArrayList()
	private var _generatedColumnNames = emptyArray<String>()

	var insertSql: String? = null
	var insertSqlArgCount = 0
	var insertColumnNames: Array<String> = emptyArray()

	var upsertSql: String? = null
	var upsertSqlArgCount = 0
	var upsertColumnNames: Array<String> = emptyArray()

	var updateSql: String? = null
	var updateColumnNames: Array<String> = emptyArray()
	var updateSqlArgCount = 0

	var selectColumns: String? = null

	init {

		try {
			if (!Map::class.java.isAssignableFrom(clazz.java)) {
				var props = populateProperties(clazz)
				val colOrder = clazz.findAnnotation<ColumnOrder>()
				if (colOrder != null) {
					// reorder the properties
					val cols: Array<String> = colOrder.value

					// props not in the cols list are ignored
					props = props.asSequence()
							.map { Pair(cols.indexOf(it.name), it) }
							.filter { it.first != -1 }
							.sortedBy { it.first }
							.map { it.second }
							.toList()
				}
				for (prop in props) {
					if (propertyMap.put(prop.name.lowercase(), prop) != null) {
						throw DbException("Duplicate pojo property found: '" + prop.name + "' in " + clazz.qualifiedName
								+ ". There may be both a field and a getter/setter")
					}
				}
			}

			table = clazz.findAnnotation<Table>()
					?.let { arrayOf(it.schema, it.name).filter { it.isNotEmpty() }.joinToString(".") }
					?: clazz.simpleName
		} catch (t: Throwable) {
			throw DbException(t)
		}
	}

	@Throws(IntrospectionException::class, InstantiationException::class, IllegalAccessException::class)
	private fun populateProperties(clazz: KClass<*>): List<FablesKProperty> {
		val props = clazz.memberProperties
				.filterIsInstance<KMutableProperty1<*, *>>()
				.mapNotNull { property ->
			if (property.visibility != KVisibility.PUBLIC) return@mapNotNull null
			if (property.getter.hasAnnotation<Transient>()) return@mapNotNull null

			var name = property.name

			val col = property.getter.findAnnotation<Column>()
			if (col != null) {
				val tmpName = col.name.trim { it <= ' ' }
				if (tmpName.isNotEmpty()) {
					name = tmpName
				}
			}

			val isPrimaryKey = property.getter.hasAnnotation<Id>()
			if (isPrimaryKey) { primaryKeyNames.add(name) }

			val isGenerated = property.getter.hasAnnotation<GeneratedValue>()

			val isEnum = property.javaField!!.type.isEnum

			// We default to STRING enum type. Can be overriden with @Enumerated annotation
			val enumType = if (isEnum) {
				property.getter.findAnnotation<Enumerated>()?.value ?: EnumType.STRING
			} else null

			val enumClass = if (isEnum) {
				property.returnType.javaType as Class<Enum<*>>
			} else null

			val c = property.getter.findAnnotation<Convert>()
			val converter = if (c != null) {
				c.converter.createInstance() as AttributeConverter<Any, Any>
			} else null

			FablesKProperty(
					name = property.name,
					property = property,
					dataType = property.returnType,
					columnAnnotation = col,
					isEnumField = isEnum,
					enumClass = enumClass,
					enumType = enumType,
					converter = converter,
					isGenerated = isGenerated,
					isPrimaryKey = isPrimaryKey
			)
		}

		_generatedColumnNames = props.filter { it.isGenerated }.map { it.name }.toTypedArray()
		return props
	}

	override fun getValue(pojo: Any?, name: String): Any? {
		try {
			val prop = propertyMap[name.lowercase()]
					?: throw DbException("No such field: $name")

			var value: Any? = prop.property.getter.call(pojo) ?: return null
			if (prop.converter != null) {
				value = prop.converter!!.convertToDatabaseColumn(value)
			} else if (prop.isEnumField) {
				// handle enums according to selected enum type
				value = if (prop.enumType == EnumType.ORDINAL) {
					(value as Enum<*>).ordinal
				} else {
					value.toString()
				}
			}
			return value
		} catch (t: Throwable) {
			throw DbException(t)
		}
	}

	override fun putValue(pojo: Any?, name: String, value: Any?) {
		putValue(pojo, name, value, false)
	}

	override fun putValue(pojo: Any?, name: String, value: Any?, ignoreIfMissing: Boolean) {
		//Bukkit.getLogger().info("putValue(name = $name, ignoreIfMissing = $ignoreIfMissing")
		var value = value
		val prop = propertyMap.get(name.lowercase())
		if (prop == null) {
			if (ignoreIfMissing) {
				return
			}
			throw DbException("No such field: $name")
		}
		if (value != null) {
			if (prop.converter != null) {
				value = prop.converter!!.convertToEntityAttribute(value)
			} else if (prop.isEnumField) {
				value = getEnumConst(prop.enumClass!!, prop.enumType!!, value)
			}
		}

		try {
			prop.property.setter.call(pojo, value)
		} catch (e: IllegalArgumentException) {
			throw DbException(
					"Could not set value into pojo. Field: " + prop.property.toString() + " value: " + value, e)
		} catch (e: IllegalAccessException) {
			throw DbException(
					"Could not set value into pojo. Field: " + prop.property.toString() + " value: " + value, e)
		}
		return
	}

	/**
	 * Convert a string to an enum const of the appropriate class.
	 */
	private fun <T : Enum<T>?> getEnumConst(enumType: Class<Enum<*>>, type: EnumType, value: Any): Enum<*> {
		val str = value.toString()
		return if (type == EnumType.ORDINAL) {
			val ordinalValue = value as Int
			if (ordinalValue < 0 || ordinalValue >= enumType.enumConstants.size) {
				throw DbException(
						"Invalid ordinal number " + ordinalValue + " for enum class " + enumType.canonicalName)
			}
			enumType.enumConstants[ordinalValue]
		} else {
			for (e in enumType.enumConstants) {
				if (str == e.toString()) {
					return e
				}
			}
			throw DbException("Enum value does not exist. value:$str")
		}
	}

	override fun getProperty(name: String): Property? {
		throw UnsupportedOperationException()
	}

	override fun getGeneratedColumnNames(): Array<String> {
		return _generatedColumnNames
	}
}
