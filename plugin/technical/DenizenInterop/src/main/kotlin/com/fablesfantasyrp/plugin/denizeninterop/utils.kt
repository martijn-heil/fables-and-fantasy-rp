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
package com.fablesfantasyrp.plugin.denizeninterop

import com.denizenscript.denizen.Denizen
import com.denizenscript.denizencore.objects.ObjectTag
import com.denizenscript.denizencore.objects.core.MapTag
import com.denizenscript.denizencore.scripts.ScriptBuilder
import com.denizenscript.denizencore.scripts.ScriptEntry
import com.denizenscript.denizencore.scripts.ScriptRegistry
import com.denizenscript.denizencore.scripts.containers.core.ProcedureScriptContainer
import com.denizenscript.denizencore.scripts.queues.core.InstantQueue
import com.denizenscript.denizencore.tags.TagManager
import com.denizenscript.denizencore.utilities.text.StringHolder

fun denizenRun(task: String, definitions: Map<String, ObjectTag>) {
	val defMap = MapTag(definitions.mapKeys { StringHolder(it.key) })

	val queue = InstantQueue("DENIZENRUN")
	queue.addDefinition("thedefmap", defMap)

	val entry = ScriptEntry("run", arrayOf(task, "defmap:<[thedefmap]>"), null, null, 1)
	queue.addEntries(listOf(entry))
	queue.start()
}

fun ex(entry: String) {
	ex(emptyMap(), entry)
}

fun ex(definitions: Map<String, ObjectTag>, vararg entries: String) {
	val scriptEntries = ScriptBuilder.buildScriptEntries(entries.toList() as List<Any>, null, null)
	scriptEntries.forEach { it.context.debug = false }
	val queue = InstantQueue("EXFUNCTION")
	definitions.forEach { queue.addDefinition(it.key, it.value) }
	queue.addEntries(scriptEntries)
	queue.start()
}

fun denizenParseTag(tag: String, definitions: Map<String, ObjectTag>): ObjectTag {
	// This is ugly, but I can't figure out how to construct a TagContext
	val locationGetKey = ScriptRegistry.getScriptContainerAs("location_get_key", ProcedureScriptContainer::class.java)
	val context = Denizen.instance.coreImplementation.getTagContext(locationGetKey).clone()

	definitions.forEach { context.definitionProvider.addDefinition(it.key, it.value) }
	val procTag = TagManager.parseTextToTag(tag, context)
	return procTag.parse(context)
}
