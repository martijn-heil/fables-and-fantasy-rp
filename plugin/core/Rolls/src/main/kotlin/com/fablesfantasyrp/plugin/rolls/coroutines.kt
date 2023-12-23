package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.utils.plaunch
import com.fablesfantasyrp.plugin.utils.prunBlocking
import kotlinx.coroutines.CoroutineScope

internal fun<T> frunBlocking(what: suspend CoroutineScope.() -> T) = prunBlocking(FablesRolls.instance, what)
internal fun flaunch(what: suspend CoroutineScope.() -> Unit) = plaunch(FablesRolls.instance, what)
