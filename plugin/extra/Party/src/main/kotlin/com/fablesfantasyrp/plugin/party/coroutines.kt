package com.fablesfantasyrp.plugin.party

import com.fablesfantasyrp.plugin.utils.plaunch
import com.fablesfantasyrp.plugin.utils.prunBlocking
import kotlinx.coroutines.CoroutineScope

internal fun<T> frunBlocking(what: suspend CoroutineScope.() -> T) = prunBlocking(FablesParty.instance, what)
internal fun flaunch(what: suspend CoroutineScope.() -> Unit) = plaunch(FablesParty.instance, what)
