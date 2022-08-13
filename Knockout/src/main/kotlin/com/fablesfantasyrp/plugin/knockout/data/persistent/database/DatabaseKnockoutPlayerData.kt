package com.fablesfantasyrp.plugin.knockout.data.persistent.database

import com.fablesfantasyrp.plugin.knockout.data.persistent.PersistentKnockoutPlayerData
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageEvent
import java.time.Instant
import java.util.*

data class DatabaseKnockoutPlayerData(override val id: UUID,
									  override var isKnockedOut: Boolean = false,
									  override var knockedOutAt: Instant? = null,
									  override var knockoutCause: EntityDamageEvent.DamageCause? = null,
									  override var knockoutDamager: Entity? = null) : PersistentKnockoutPlayerData {

}
