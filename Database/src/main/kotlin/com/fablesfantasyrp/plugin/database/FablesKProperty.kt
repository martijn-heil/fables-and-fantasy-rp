package com.fablesfantasyrp.plugin.database

import javax.persistence.AttributeConverter
import javax.persistence.Column
import javax.persistence.EnumType
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType

data class FablesKProperty(
		var name: String,
		var property: KMutableProperty1<*, *>,
		var dataType: KType,
		var isGenerated: Boolean = false,
		var isPrimaryKey: Boolean = false,
		var isEnumField: Boolean = false,
		var enumClass: Class<Enum<*>>? = null,
		var enumType: EnumType? = null,
		var columnAnnotation: Column? = null,
		var converter: AttributeConverter<Any, Any>? = null)
