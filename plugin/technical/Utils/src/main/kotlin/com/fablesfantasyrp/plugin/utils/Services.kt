package com.fablesfantasyrp.plugin.utils

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.koin.core.context.GlobalContext
import kotlin.reflect.KClass

object Services {
	inline fun<reified T: Any> get()
		= (Bukkit.getServicesManager().getRegistration(T::class.java)?.provider
			?: GlobalContext.getOrNull()?.getOrNull<T>())!!

	inline fun<reified T: Any> getMaybe()
		= Bukkit.getServicesManager().getRegistration(T::class.java)?.provider
			?: GlobalContext.getOrNull()?.getOrNull<T>()

	inline fun<reified T: Any> register(clazz: KClass<T>, instance: T, plugin: Plugin, priority: ServicePriority
		= ServicePriority.Normal) = Bukkit.getServicesManager().register(clazz.java, instance, plugin, priority)
}
