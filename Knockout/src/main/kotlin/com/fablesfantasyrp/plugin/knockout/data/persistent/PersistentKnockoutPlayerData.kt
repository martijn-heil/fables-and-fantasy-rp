package com.fablesfantasyrp.plugin.knockout.data.persistent

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageEvent
import java.time.Instant
import java.util.*

interface PersistentKnockoutPlayerData : Identifiable<UUID> {
	val isKnockedOut: Boolean get() = knockedOutAt != null
	var knockedOutAt: Instant?
	var knockoutCause: EntityDamageEvent.DamageCause?
	var knockoutDamager: Entity?
}
