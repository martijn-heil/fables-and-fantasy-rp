package com.fablesfantasyrp.plugin.utils.validation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

open class CommandValidationException(val component: Component)
	: Exception(PlainTextComponentSerializer.plainText().serialize(component)) {

	constructor(message: String) : this(Component.text(message))
}
