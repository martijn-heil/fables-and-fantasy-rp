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
