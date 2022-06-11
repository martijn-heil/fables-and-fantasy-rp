package com.fablesfantasyrp.plugin.denizeninterop

import com.denizenscript.denizencore.objects.ObjectTag
import com.denizenscript.denizencore.objects.core.MapTag
import com.denizenscript.denizencore.scripts.ScriptBuilder
import com.denizenscript.denizencore.scripts.ScriptEntry
import com.denizenscript.denizencore.scripts.queues.core.InstantQueue
import com.denizenscript.denizencore.utilities.text.StringHolder

fun denizenRun(task: String, definitions: Map<String, ObjectTag>) {
	val defMap = MapTag(definitions.mapKeys { StringHolder(it.key) })

	val queue = InstantQueue("DENIZENRUN")
	queue.addDefinition("thedefmap", defMap)

	val entry = ScriptEntry("run", arrayOf(task, "defmap:<[thedefmap]>"), null, null, 1)
	entry.shouldDebugBool = false
	queue.addEntries(listOf(entry))
	queue.start()
}

fun ex(entry: String) {
	val entries: List<Any> = listOf(entry)
	val scriptEntries = ScriptBuilder.buildScriptEntries(entries, null, null)
	scriptEntries.forEach { it.shouldDebugBool = false }
	val queue = InstantQueue("EXFUNCTION")
	queue.addEntries(scriptEntries)
	queue.start()
}
