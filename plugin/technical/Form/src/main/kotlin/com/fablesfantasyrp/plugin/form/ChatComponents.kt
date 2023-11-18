package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.text.miniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

fun button(text: String, clickable: Clickable): Component {
	return buttonText(text)
		.clickable(clickable)
}

fun button(text: String, description: String, clickable: Clickable): Component {
	return buttonText(text)
		.hoverEvent(HoverEvent.showText(Component.text(description)))
		.clickable(clickable)
}

fun button(text: String, clickEvent: ClickEvent): Component {
	return buttonText(text)
		.clickEvent(clickEvent)
}

fun button(text: String, description: String, clickEvent: ClickEvent): Component {
	return buttonText(text)
		.hoverEvent(HoverEvent.showText(Component.text(description)))
		.clickEvent(clickEvent)
}

private fun buttonText(text: String)
	= miniMessage.deserialize("<green><bold>[ <text> ]</bold></green>", Placeholder.unparsed("text", text.uppercase()))
