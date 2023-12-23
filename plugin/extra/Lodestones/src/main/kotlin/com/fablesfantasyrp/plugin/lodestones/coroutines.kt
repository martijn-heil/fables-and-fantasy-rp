package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.utils.plaunch
import com.fablesfantasyrp.plugin.utils.prunBlocking
import kotlinx.coroutines.CoroutineScope

internal fun<T> frunBlocking(what: suspend CoroutineScope.() -> T) = prunBlocking(FablesLodestones.instance, what)
internal fun flaunch(what: suspend CoroutineScope.() -> Unit) = plaunch(FablesLodestones.instance, what)
