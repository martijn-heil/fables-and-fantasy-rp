package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.mageRepository
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class MageTypeConverter : AttributeConverter<Mage, Long> {
	override fun convertToDatabaseColumn(attribute: Mage): Long = attribute.id
	override fun convertToEntityAttribute(dbData: Long): Mage = mageRepository.forId(dbData)!!
}
