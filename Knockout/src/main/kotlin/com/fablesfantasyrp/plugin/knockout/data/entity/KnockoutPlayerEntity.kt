package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.knockout.data.KnockoutPlayerData
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

interface KnockoutPlayerEntity : KnockoutPlayerData {
	fun knockout(cause: EntityDamageEvent.DamageCause?, by: Entity?)
	fun revive(by: Player?)
	fun execute(cause: EntityDamageEvent.DamageCause?, by: Entity?)
}
