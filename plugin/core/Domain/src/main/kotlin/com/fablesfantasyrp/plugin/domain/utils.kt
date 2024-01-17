package com.fablesfantasyrp.plugin.domain

import org.bukkit.NamespacedKey

fun namespaced(key: String) = NamespacedKey(NAMESPACE, key)
