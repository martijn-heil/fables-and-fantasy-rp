package com.fablesfantasyrp.plugin.discord

import com.fablesfantasyrp.plugin.utils.plaunch
import com.fablesfantasyrp.plugin.utils.prunBlocking
import kotlinx.coroutines.CoroutineScope

internal fun<T> frunBlocking(what: suspend CoroutineScope.() -> T) = prunBlocking(FablesDiscord.instance, what)
internal fun flaunch(what: suspend CoroutineScope.() -> Unit) = plaunch(FablesDiscord.instance, what)
