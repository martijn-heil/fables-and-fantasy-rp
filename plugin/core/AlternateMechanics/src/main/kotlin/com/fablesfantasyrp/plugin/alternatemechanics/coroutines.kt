package com.fablesfantasyrp.plugin.alternatemechanics

import com.fablesfantasyrp.plugin.utils.plaunch
import com.fablesfantasyrp.plugin.utils.prunBlocking
import kotlinx.coroutines.CoroutineScope

internal fun<T> frunBlocking(what: suspend CoroutineScope.() -> T) = prunBlocking(FablesAlternateMechanics.instance, what)
internal fun flaunch(what: suspend CoroutineScope.() -> Unit) = plaunch(FablesAlternateMechanics.instance, what)
