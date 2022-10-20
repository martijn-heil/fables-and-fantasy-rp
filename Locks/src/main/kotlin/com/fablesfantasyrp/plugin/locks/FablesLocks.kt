package com.fablesfantasyrp.plugin.locks

import com.fablesfantasyrp.plugin.locks.data.SimpleLockDataRepository
import com.fablesfantasyrp.plugin.locks.data.persistent.DenizenSimpleLockDataRepository
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin

class FablesLocks : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		lockRepository = DenizenSimpleLockDataRepository()
	}

	companion object {
		lateinit var instance: FablesLocks
		lateinit var lockRepository: SimpleLockDataRepository
			private set
	}
}
